import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.stream.Collectors;
import utils.Tuple;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import anomaly.Anomaly;
import anomaly.AnomalyStructure;
import ar.ddl.Table;
import cons.ConstantArgs;
import exceptions.UnknownUnitException;
import fec.GimpToAppOne;
import fec.utils.DDLParser;
import ar.Application;
import ar.Transaction;
import soot.Body;
import soot.BodyTransformer;
import soot.PhaseOptions;
import soot.Scene;
import soot.jimple.JimpleBody;
import soot.util.cfgcmd.CFGIntermediateRep;
import Z3.Z3Driver;

/**
 * 
 * @author Kia Rahmani (https://kiarahmani.github.io/)
 * 
 */

public class Transformer extends BodyTransformer {
	private static final Logger LOG = LogManager.getLogger(Transformer.class);
	static long start_fec, end_fec;
	//static String _RT_PATH = "/Library/Java/JavaVirtualMachines/jdk1.8.0_77.jdk/Contents/Home/jre/lib/rt.jar:";
	//static String _JCE_PATH = "/Library/Java/JavaVirtualMachines/jdk1.8.0_77.jdk/Contents/Home/jre/lib/jce.jar";
	private static final String irOptionName = "ir";
	private CFGIntermediateRep ir;
	static ArrayList<Body> bodies;

	//private static final List<String> choppedDirtyReadPattern = List.of("step_sibling", "WR", "RW");
	//private static final List<String> nonRepeatableReadPattern = List.of("X", "RW", "WR");
	//private static final List<String> choppedNonRepeatableReadPattern = List.of("step_sibling", "RW", "WR");
	//private static final List<String> choppedDirtyWritePattern = List.of("step_sibling", "WW", "WW");
	//private static final List<String> choppedLostUpdateWriteSkewPattern = List.of("step_sibling", "RW", "step_sibling", "RW");
	//private static final List<String> choppedReadSkewPattern = List.of("step_sibling", "RW", "step_sibling", "WR");
	private static final List<String> dirtyReadPattern = Arrays.asList(new String[]{"X", "WR", "RW"});
	private static final List<String> dirtyWritePattern = Arrays.asList(new String[]{"X", "WW", "WW"});
	private static final List<String> dirtyWritePattern2 = Arrays.asList(new String[]{"X", "WW", "X", "WW"});
	private static final List<String> lostUpdateWriteSkewPattern = Arrays.asList(new String[]{"X", "RW", "X", "RW"});
	private static final List<String> lostUpdatePattern2 = Arrays.asList(new String[]{"X", "RW", "WW"});
	private static final List<String> readSkewPattern = Arrays.asList(new String[]{"X", "RW", "X", "WR"});
	private static final List<String> readSkewPattern2 = Arrays.asList(new String[]{"X", "WR", "X", "RW"});
	private static final List<String> UnknownPattern = Arrays.asList(new String[]{"OTHER"});

	private static final Map<List<String>, String> structure2anmlName;
    static {
        Map<List<String>, String> auxMap = new HashMap<List<String>, String>();
		//auxMap.put(choppedDirtyReadPattern, "Dirty Read");
		//auxMap.put(nonRepeatableReadPattern, "Non-repeatable Read");
		//auxMap.put(choppedNonRepeatableReadPattern, "Non-repeatable Read");
		//auxMap.put(choppedDirtyWritePattern, "Dirty Write");
		//auxMap.put(choppedLostUpdateWriteSkewPattern, "Lost Update/Write Skew");
		//auxMap.put(choppedReadSkewPattern, "Read Skew");
        auxMap.put(dirtyReadPattern, "Dirty Reads");
		auxMap.put(dirtyWritePattern, "Dirty Writes");
		auxMap.put(dirtyWritePattern2, "Dirty Writes");
		auxMap.put(lostUpdateWriteSkewPattern, "Lost Updates/Write Skews");
		auxMap.put(lostUpdatePattern2, "Lost Updates");
		auxMap.put(readSkewPattern, "Read Skews");
		auxMap.put(readSkewPattern2, "Read Skews");
		auxMap.put(UnknownPattern, "Others");
        structure2anmlName = auxMap;
    }

	@Override
	protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
		if (bodies == null)
			bodies = new ArrayList<Body>();
		// options.put("brief", "fa");
		Map<String, String> modifiedOptions = new HashMap<String, String>();
		for (String option : options.keySet())
			modifiedOptions.put(option, options.get(option));

		modifiedOptions.put("use-original-names", "true");
		ir = CFGIntermediateRep.getIR(PhaseOptions.getString(modifiedOptions, irOptionName));
		Body body = ir.getBody((JimpleBody) b);
		if (!body.getMethod().isConstructor())
			bodies.add(body);
	}

	public static void main(String[] args) {
		Application app = null;
		Anomaly anml1 = null, anml2 = null;
		int iter = 1;
		new ConstantArgs();
		LOG.info("Beginnign of the main method");
		LOG.info("Calling initialize method from Initializer");
		String[] soot_args = new Initializer().initialize();
		soot.Main.main(soot_args);
		LOG.info("Soot results returned");
		// extract tables from ddl file
		LOG.info("Frontend compilation begin");
		start_fec = System.currentTimeMillis();
		DDLParser ddlp = new DDLParser();
		ArrayList<Table> tables = ddlp.parse();
		LOG.info("Tables extracted");
		LOG.info("Beginning transaction extraction");
		// extract transactions
		try {
			app = (new GimpToAppOne(Scene.v(), bodies, tables)).transform();
		} catch (UnknownUnitException e) {
			e.printStackTrace();
		}
		end_fec = System.currentTimeMillis();
		if (ConstantArgs.EXTRACT_ONLY) {
			Map<String, Integer> anmlsCountersEmpty = new HashMap<String, Integer>();
			printStats(app, tables, anmlsCountersEmpty, -1, -1, -1);
			return;
		}

		/*
		 * Begin AR analysis
		 */
		Z3Driver zdr;
		AnomalyStructure seenStructures = new AnomalyStructure();
		if (ConstantArgs._CONTINUED_ANALYSIS) {
			try {
				seenStructures.load();
			} catch (ClassNotFoundException | IOException e1) {
				LOG.error("_CONTINUED_ANALYSIS is set to true but loading failed: make sure files exist");
			}
		}

		Set<List<String>> txnsNamesCombsSet = new HashSet<>();
		// Assuming that the cycle max length is 4, so max combinations size is 3 original transactions
		for (int i = 0; i < app.getOrigTxns().size(); i++) {
			for (int j = i; j < app.getOrigTxns().size(); j++) {
				for (int k = j; k < app.getOrigTxns().size(); k++) {
					Set<String> txnsNamesCombSet = new HashSet<>();
					txnsNamesCombSet.addAll(Arrays.asList(app.getOrigTxns().get(i).getName(), app.getOrigTxns().get(j).getName(), app.getOrigTxns().get(k).getName()));
					txnsNamesCombsSet.add(new ArrayList<>(txnsNamesCombSet));
				}
			}
		}
		List<List<String>> txnsNamesCombs = new ArrayList<>(txnsNamesCombsSet);
		Collections.sort(txnsNamesCombs, (txnsNamesComb1, txnsNamesComb2) -> Integer.compare(txnsNamesComb1.size(), txnsNamesComb2.size()));
		
		if (app.getOrigTxns().size() >= 3)
			txnsNamesCombs.removeIf(l -> l.size() < 3);
		
		int txnsNamesCombIdx = 0;
		long analysis_begin_time = System.currentTimeMillis();
		List<Anomaly> seenAnmls = new ArrayList<>();
		List<Anomaly> seenVersAnmls = new ArrayList<>();
		// Outermost loop to iterate over different partition sizes
		while (ConstantArgs._current_partition_size <= ConstantArgs._MAX_NUM_PARTS) {
			LOG.info("Begin partition size " + ConstantArgs._current_partition_size + "");
			int currentRowInstLimit = 1;
			// the following check is necessary to prevent infinite looping
			if (ConstantArgs._MAX_ROW_INSTANCES > tables.size() && ConstantArgs._ENFORCE_ROW_INSTANCE_LIMITS) {
				LOG.fatal("_MAX_ROW_INSTANCES (=" + ConstantArgs._MAX_ROW_INSTANCES
						+ ") cannot be greater than tables.size (=" + tables.size() + ")");
				return;
			}
			// Iterate over different numbers of tables involved in each anomaly
			// Iterate over [1,table.size()]
			while (currentRowInstLimit <= ConstantArgs._MAX_ROW_INSTANCES) {
				if (!ConstantArgs._ENFORCE_ROW_INSTANCE_LIMITS) {
					LOG.info("_ENFORCE_ROW_INSTANCE_LIMITS is set to false. All tables are included");
					currentRowInstLimit = tables.size();
				} else
					;// LOG.info("Begin currentRowInstLimit " + currentRowInstLimit + "");
				for (Set<Table> includedTables : getAllTablesPerms(tables, currentRowInstLimit)) {
					System.out.println();
					LOG.info("Begin analysis for tables: "
							+ includedTables.stream().map(t -> t.getName()).collect(Collectors.toSet()));
					ConstantArgs._Current_Cycle_Length = ConstantArgs._Minimum_Cycle_Length;
					// Iterate over different anomaly lengths
					do {
						LOG.info("New round of analysis for an anomaly of length: "
								+ ConstantArgs._Current_Cycle_Length);
						LOG.info("Analysis for transactions: "
														+ txnsNamesCombs.get(txnsNamesCombIdx));
						try {
							seenStructures.save();
							LOG.info("All models saved in file");
						} catch (IOException e) {
							e.printStackTrace();
						}
						long anml2_begin_time = System.currentTimeMillis();
						anml2 = null;
						zdr = new Z3Driver(app, tables, false);
						LOG.info("New Z3Driver created");
						ConstantArgs._current_version_enforcement = false;
						anml1 = zdr.analyze(1, seenStructures.getStructures(), seenAnmls, includedTables, null, txnsNamesCombs.get(txnsNamesCombIdx));
						if (anml1 != null) {
							LOG.info("Unversioned anomaly generated: " + anml1);
							anml1.generateCycleStructure();
							System.out.println("structure1: "+anml1.getCycleStructure());
							seenAnmls.add(anml1);
							seenStructures.addStructure(anml1.getCycleStructure());
							seenStructures.writeToCSV(seenStructures.size(), iter - 1, anml1);
							// Versioned analysis
							ConstantArgs._current_version_enforcement = true;
							anml2 = zdr.analyze(2, null, seenAnmls, includedTables, anml1, txnsNamesCombs.get(txnsNamesCombIdx));
							if (anml2 != null) {

								anml2.generateCycleStructure();
								System.out.println("structure3: "+anml2.getCycleStructure());
								seenVersAnmls.add(anml2);
								// seenStructures.addStructure(anml2.getCycleStructure());
								// seenStructures.writeToCSV(seenStructures.size(), iter - 1, anml2);
								long anml2_finish_time = System.currentTimeMillis();
								anml2.setExtractionTime(-1, anml2_finish_time - anml2_begin_time);
								// anml2.announce(false, seenStructures.size());
								// LOG.info("Versioned anomaly generated (" + seenStructures.size() + ") -- " + anml2);
								
								// Commented since it is not being used and contributing to an error
								// anml2.announce(false, seenVersAnmls.size());
								LOG.info("Versioned anomaly generated (" + seenVersAnmls.size() + ") -- " + anml2);

								// inner loop for finding structurally similar anomalies
								if (ConstantArgs._ENFORCE_OPTIMIZED_ALGORITHM) {
									LOG.info("Entering the inner loop for finding structurally similar anomalies");
									Anomaly anml3 = zdr.analyze(3, null, seenAnmls, includedTables, anml2, txnsNamesCombs.get(txnsNamesCombIdx));
									if (anml3 == null)
										LOG.info("No structurally similar anomaly exists");
									while (anml3 != null) {
										anml3.generateCycleStructure();
										System.out.println("structure3: "+anml3.getCycleStructure());
										seenAnmls.add(anml3);
										seenVersAnmls.add(anml3);
										seenStructures.addStructure(anml3.getCycleStructure());
										seenStructures.writeToCSV(seenStructures.size(), iter - 1, anml3);
										LOG.info("A structurally similar anomaly generated (" + seenVersAnmls.size()
												+ ") -- " + anml3);
										
										// Commented since it is not being used and contributing to an error
										// anml3.announce(false, seenVersAnmls.size());
										
										// repeat
										anml3 = zdr.analyze(4, null, seenAnmls, includedTables, anml3, null);
									}
								}
							} else
								LOG.info("No versioning exists for: " + anml1);
							anml1.closeCtx();

						} else {
							zdr.closeCtx();
							LOG.info("No anomaly was found");
						}
						// No more anomalies using that set of transactions
						if (anml1 == null)
							txnsNamesCombIdx++;
						// update global variables for the next round
						if (/*anml2 == null || */anml1 == null && txnsNamesCombIdx == txnsNamesCombs.size()) {
							txnsNamesCombIdx = 0;
							LOG.info("Search completed for anomalies of length: " + ConstantArgs._Current_Cycle_Length);
							ConstantArgs._Current_Cycle_Length++;
						}
					} while (ConstantArgs._Current_Cycle_Length <= ConstantArgs._MAX_CYCLE_LENGTH);
				}
				currentRowInstLimit++;
			}
			ConstantArgs._current_partition_size++;
			iter++;
		}
		long analysis_finish_time = System.currentTimeMillis();

		Map<String, Integer> anmlsCounters = new HashMap<String, Integer>();
		for(String anmlName : structure2anmlName.values())
			anmlsCounters.put(anmlName, 0);

		Integer currentCounterValue = 0;
		Map<List<String>, Integer> txnsInteractions = new HashMap<List<String>, Integer>();
		Map<String, Integer> txnsAppearance = new HashMap<String, Integer>();
		String anmlName = "";
		for (Anomaly seenVersAnml : seenVersAnmls) {
			List<String> edges = new ArrayList<>();
			List<String> seenTxns = new ArrayList<>();
			for (Tuple<String, Tuple<String, String>> edge : seenVersAnml.getCycleStructure()) {
				if(edge.x.contains("sibling")) {
					edges.add("X");
				} else {
					edges.add(edge.x);
				}

				Tuple<String,String> relatedOps = edge.y;
				String leftTxn = relatedOps.x.split("-")[0];
				leftTxn = leftTxn.substring(1,leftTxn.length());

				if (seenTxns.contains(leftTxn)) continue;

				if (!txnsAppearance.containsKey(leftTxn)) {
					txnsAppearance.put(leftTxn, 1);
				} else {
					txnsAppearance.put(leftTxn, txnsAppearance.get(leftTxn)+1);
				}
				seenTxns.add(leftTxn);
			}

			Collections.sort(seenTxns);
			
			if(!txnsInteractions.containsKey(seenTxns)) {
				txnsInteractions.put(seenTxns, 1);
			} else {
				txnsInteractions.put(seenTxns, txnsInteractions.get(seenTxns)+1);
			}

			// Unknown anomaly
			if(!structure2anmlName.containsKey(edges)) {
				edges.clear();
				edges.add("OTHER");
			}

			anmlName = structure2anmlName.get(edges);
			currentCounterValue = anmlsCounters.get(anmlName);
			anmlsCounters.put(anmlName, ++currentCounterValue);
		}

		txnsInteractions.forEach((k, v) ->  System.out.println(k + ": " + v + "/"+seenVersAnmls.size()));

		for(String txnName : txnsAppearance.keySet().stream().sorted().collect(Collectors.toList())) {
			Transaction t = app.getTxnByName(txnName);
			String origTxnName = t.getOriginalTransaction();
			int txnCount = txnsAppearance.get(txnName);
			if (origTxnName != null && !origTxnName.equals(txnName)) {
				System.out.println(origTxnName + " (" + txnName + "): " + txnCount + "/"+seenVersAnmls.size());
			} else {
				System.out.println(txnName + ": " + txnCount + "/"+seenVersAnmls.size());
			}
		}

		printStats(app, tables, anmlsCounters, seenVersAnmls.size(), (analysis_finish_time - analysis_begin_time),
				(analysis_finish_time - analysis_begin_time) / (iter - 1));
	}

	// return all subsets of all tables up the given bound r
	private static List<Set<Table>> getAllTablesPerms(ArrayList<Table> tables, int r) {
		List<Set<Table>> result = new ArrayList<>();
		Table[] arr = tables.toArray(new Table[tables.size()]);
		int n = arr.length;
		Table data[] = new Table[r];
		combinationUtil(arr, n, r, 0, data, 0, result);
		return result;
	}

	private static void combinationUtil(Table arr[], int n, int r, int index, Table data[], int i,
			List<Set<Table>> resList) {
		if (index == r) {
			Set<Table> resSet = new HashSet<>();
			for (int j = 0; j < r; j++)
				resSet.add(data[j]);
			resList.add(resSet);
			return;
		}
		if (i >= n)
			return;
		data[index] = arr[i];
		combinationUtil(arr, n, r, index + 1, data, i + 1, resList);
		combinationUtil(arr, n, r, index, data, i + 1, resList);
	}

	// Print analysis stats
	private static void printStats(Application app, ArrayList<Table> tables, Map<String, Integer> anmlsCounters, int anmlCount, 
			long modelsTime, long avgExt) {
		System.out.println("\n------------------------------------------------------"
				+ "\n 			     Schema\n"
				+ "------------------------------------------------------");
		for (Table t : tables)
			t.printTable();
		System.out.println();
		app.printApp();
		System.out.println("\n\n\n\n===========================================");
		System.out.println("=== AR compile time:	" + (end_fec - start_fec) + "ms");
		
		System.out.println("-------------------------------------------");
		for(String anmlName : anmlsCounters.keySet().stream().sorted().collect(Collectors.toList())) {
			if(anmlName.equals("Lost Updates/Write Skews"))
				System.out.println("+++ " + anmlName + " found:	" + anmlsCounters.get(anmlName));
			else if(!anmlName.equals("Others"))
				System.out.println("+++ " + anmlName + " found:		" + anmlsCounters.get(anmlName));
		}
		System.out.println("+++ Others found:			" + anmlsCounters.get("Others"));
		System.out.println("-------------------------------------------");
		
		System.out.println("=== Total anomalies found:	" + anmlCount);
		System.out.println("=== Analysis time:		" + modelsTime + " ms");
		//System.out.println("=== Avg Ext. Time:		" + avgExt + " ms");

	}

}

/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
