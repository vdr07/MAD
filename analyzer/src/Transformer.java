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
import ar.OriginalTransaction;
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
		// Anomaly anml1 = null, anml2 = null;
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
		// Z3Driver zdr;
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
		for (int i = 0; i < app.getOrigTxns().size(); i++)
			for (int j = i; j < app.getOrigTxns().size(); j++)
				for (int k = j; k < app.getOrigTxns().size(); k++) {
					Set<String> txnsNamesCombSet = new HashSet<>();
					txnsNamesCombSet.addAll(Arrays.asList(app.getOrigTxns().get(i).getName(), app.getOrigTxns().get(j).getName(), app.getOrigTxns().get(k).getName()));
					txnsNamesCombsSet.add(new ArrayList<>(txnsNamesCombSet));
				}

		List<List<String>> txnsNamesCombs = new ArrayList<>(txnsNamesCombsSet);

		Collections.sort(txnsNamesCombs, (txnsNamesComb1, txnsNamesComb2) -> Integer.compare(txnsNamesComb1.size(), txnsNamesComb2.size()));
		
		Map<Integer, Integer> numCombSizesOccurrences = new HashMap<Integer, Integer>();
		for (List<String> txnsNamesComb : txnsNamesCombs) {
			int combSize = txnsNamesComb.size();
			numCombSizesOccurrences.put(combSize, numCombSizesOccurrences.getOrDefault(combSize, 0) + 1);
		}

		long analysis_begin_time = System.currentTimeMillis();
		List<Anomaly> seenAnmls = Collections.synchronizedList(new ArrayList<>());
		List<Anomaly> seenVersAnmls = Collections.synchronizedList(new ArrayList<>());
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
					
					for (int txnsNamesCombIdx = 0; txnsNamesCombIdx < txnsNamesCombs.size(); txnsNamesCombIdx++) {
						// Iterate over different anomaly lengths
						int current_cycle_length = ConstantArgs._Minimum_Cycle_Length;
						do {
							LOG.info("New round of analysis for an anomaly of length: "
									+ current_cycle_length);
							LOG.info("Analysis for transactions: "
															+ txnsNamesCombs.get(txnsNamesCombIdx));
							try {
								seenStructures.save();
								LOG.info("All models saved in file");
							} catch (IOException e) {
								e.printStackTrace();
							}
							long anml2_begin_time = System.currentTimeMillis();
							Z3Driver zdr = new Z3Driver(app, tables, false);
							// anml2 = null;
							LOG.info("New Z3Driver created");
							Anomaly anml1 = zdr.analyze(1, seenStructures.getStructures(), seenAnmls, includedTables, null, txnsNamesCombs.get(txnsNamesCombIdx), current_cycle_length, false);
							if (anml1 != null) {
								LOG.info("Unversioned anomaly generated: " + anml1);
								anml1.generateCycleStructure();
								System.out.println("structure1: "+anml1.getCycleStructure());
								seenAnmls.add(anml1);
								seenStructures.addStructure(anml1.getCycleStructure());
								seenStructures.writeToCSV(seenStructures.size(), /*iter - 1*/ 0, anml1);
								// Versioned analysis
								Anomaly anml2 = zdr.analyze(2, null, seenAnmls, includedTables, anml1, txnsNamesCombs.get(txnsNamesCombIdx), current_cycle_length, true);
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
									
									anml2.announce(false, seenVersAnmls.size());
									LOG.info("Versioned anomaly generated (" + seenVersAnmls.size() + ") -- " + anml2);

									// inner loop for finding structurally similar anomalies
									if (ConstantArgs._ENFORCE_OPTIMIZED_ALGORITHM) {
										LOG.info("Entering the inner loop for finding structurally similar anomalies");
										Anomaly anml3 = zdr.analyze(3, null, seenAnmls, includedTables, anml2, txnsNamesCombs.get(txnsNamesCombIdx), current_cycle_length, true);
										if (anml3 == null)
											LOG.info("No structurally similar anomaly exists");
										while (anml3 != null) {
											anml3.generateCycleStructure();
											System.out.println("structure3: "+anml3.getCycleStructure());
											seenAnmls.add(anml3);
											seenVersAnmls.add(anml3);
											seenStructures.addStructure(anml3.getCycleStructure());
											seenStructures.writeToCSV(seenStructures.size(), /*iter - 1*/ 0, anml3);
											LOG.info("A structurally similar anomaly generated (" + seenVersAnmls.size()
													+ ") -- " + anml3);
											
											anml3.announce(false, seenVersAnmls.size());
											
											// repeat
											anml3 = zdr.analyze(4, null, seenAnmls, includedTables, anml3, null, 0, true);
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
							//if (anml1 == null)
								//txnsNamesCombIdx++;
							// update global variables for the next round
							if (/*anml2 == null || */anml1 == null) {
								LOG.info("Search completed for anomalies of length: " + current_cycle_length);
								current_cycle_length++;
							}
						} while (current_cycle_length <= ConstantArgs._MAX_CYCLE_LENGTH);
					}
				}
				currentRowInstLimit++;
			}
			ConstantArgs._current_partition_size++;
			iter++;
		}
		
		long analysis_finish_time = System.currentTimeMillis();

		Map<List<Tuple<String, String>>, String> anmlsTypesPatterns = getAnmlTypesPatterns();

		Map<String, Integer> anmlsCounters = new HashMap<String, Integer>();
		for(String anmlName : anmlsTypesPatterns.values())
			anmlsCounters.put(anmlName, 0);
		
		Integer currentCounterValue = 0;
		Map<List<String>, Integer> txnsInteractions = new HashMap<List<String>, Integer>();
		Map<String, Integer> txnsAppearance = new HashMap<String, Integer>();
		String anmlName = "";
		for (Anomaly seenVersAnml : seenVersAnmls) {
			List<String> seenTxns = new ArrayList<>();
			List<Tuple<String, String>> edgeTypesLeftOps = new ArrayList<>(); 
			for (Tuple<String, Tuple<String, String>> edge : seenVersAnml.getCycleStructure()) {
				String edgeType;
				if(edge.x.contains("sibling")) {
					edgeType = "X";
				} else {
					edgeType = edge.x; 
				}
				int leftIndex = edge.y.x.indexOf("-");
				int rightIndex = edge.y.x.indexOf("#", leftIndex + 1);
				String leftOpType = edge.y.x.substring(leftIndex+1, rightIndex);
				Tuple<String, String> edgeTypeLeftOp = new Tuple<String, String>(edgeType, leftOpType);
				edgeTypesLeftOps.add(edgeTypeLeftOp);

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
			if(!anmlsTypesPatterns.containsKey(edgeTypesLeftOps)) {
				// shift two positions in case the edges are in a different order e.g. "X WW X RW", instead of "X RW X WW"
				int size = edgeTypesLeftOps.size();
				List<Tuple<String, String>> temp = new ArrayList<>(edgeTypesLeftOps.subList(size - 2, size));
				temp.addAll(edgeTypesLeftOps.subList(0, size - 2));

				for (int j = 0; j < size; j++) {
					edgeTypesLeftOps.set(j, temp.get(j));
				}

				if(!anmlsTypesPatterns.containsKey(edgeTypesLeftOps)) {
					edgeTypesLeftOps.clear();
					edgeTypesLeftOps.add(new Tuple<String, String>("X", "extension"));
				}
			}

			anmlName = anmlsTypesPatterns.get(edgeTypesLeftOps);
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

	private static Map<List<Tuple<String, String>>, String> getAnmlTypesPatterns() {
		Map<List<Tuple<String, String>>, String> anmlsTypesPatterns = new HashMap<List<Tuple<String, String>>, String>();
        ArrayList<Tuple<String, String>> dirtyReadPattern_1 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("WR","update"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> nonRepeatableReadPattern_1 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","select"), new Tuple<>("RW","select"), new Tuple<>("WR","update")));
		ArrayList<Tuple<String, String>> phantomReadPattern_1 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","select"), new Tuple<>("RW","select"), new Tuple<>("WR","insert")));
		ArrayList<Tuple<String, String>> phantomReadPattern_2 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","select"), new Tuple<>("RW","select"), new Tuple<>("WR","delete")));
		ArrayList<Tuple<String, String>> phantomReadPattern_3 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","select"), new Tuple<>("RW","select"), new Tuple<>("X","insert"), new Tuple<>("WR","insert")));
		ArrayList<Tuple<String, String>> phantomReadPattern_4 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","select"), new Tuple<>("RW","select"), new Tuple<>("X","delete"), new Tuple<>("WR","delete")));
		ArrayList<Tuple<String, String>> dirtyWritePattern_1 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("WW","update"), new Tuple<>("X","update"), new Tuple<>("WW","update")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_1 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","select"), new Tuple<>("X","update"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_2 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","select"), new Tuple<>("X","update"), new Tuple<>("WW","update")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_3 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","select"), new Tuple<>("X","update"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_4 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","select"), new Tuple<>("X","update"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_5 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","select"), new Tuple<>("X","insert"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_6 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","select"), new Tuple<>("X","insert"), new Tuple<>("WW","update")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_7 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","select"), new Tuple<>("X","insert"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_8 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","select"), new Tuple<>("X","insert"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_9 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","select"), new Tuple<>("X","delete"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_10 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","select"), new Tuple<>("X","delete"), new Tuple<>("WW","update")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_11 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","select"), new Tuple<>("X","delete"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_12 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","select"), new Tuple<>("X","delete"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_13 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("RW","select"), new Tuple<>("X","update"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_14 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("RW","select"), new Tuple<>("X","update"), new Tuple<>("RW","update")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_15 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("RW","select"), new Tuple<>("X","update"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_16 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("RW","select"), new Tuple<>("X","update"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_17 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("RW","select"), new Tuple<>("X","insert"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_18 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("RW","select"), new Tuple<>("X","insert"), new Tuple<>("RW","update")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_19 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("RW","select"), new Tuple<>("X","insert"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_20 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("RW","select"), new Tuple<>("X","insert"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_21 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("RW","select"), new Tuple<>("X","delete"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_22 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("RW","select"), new Tuple<>("X","delete"), new Tuple<>("RW","update")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_23 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("RW","select"), new Tuple<>("X","delete"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_24 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("RW","select"), new Tuple<>("X","delete"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_25 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("RW","select"), new Tuple<>("X","update"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_26 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("RW","select"), new Tuple<>("X","update"), new Tuple<>("RW","update")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_27 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("RW","select"), new Tuple<>("X","update"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_28 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("RW","select"), new Tuple<>("X","update"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_29 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("RW","select"), new Tuple<>("X","insert"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_30 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("RW","select"), new Tuple<>("X","insert"), new Tuple<>("RW","update")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_31 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("RW","select"), new Tuple<>("X","insert"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_32 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("RW","select"), new Tuple<>("X","insert"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_33 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("RW","select"), new Tuple<>("X","delete"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_34 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("RW","select"), new Tuple<>("X","delete"), new Tuple<>("RW","update")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_35 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("RW","select"), new Tuple<>("X","delete"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdateWriteSkew_36 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("RW","select"), new Tuple<>("X","delete"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_1 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","select"), new Tuple<>("WW","update")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_2 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","select"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_3 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","select"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_4 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("RW","select"), new Tuple<>("WW","update")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_5 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("RW","select"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_6 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("RW","select"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_7 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("RW","select"), new Tuple<>("WW","update")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_8 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("RW","select"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_9 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("RW","select"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_10 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("WW","update"), new Tuple<>("WW","update")));
        ArrayList<Tuple<String, String>> lostUpdatePattern_11 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","update"), new Tuple<>("WW","insert")));
        ArrayList<Tuple<String, String>> lostUpdatePattern_12 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("RW","update"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_13 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("WW","insert"), new Tuple<>("WW","update")));
        ArrayList<Tuple<String, String>> lostUpdatePattern_14 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("WW","insert"), new Tuple<>("WW","insert")));
        ArrayList<Tuple<String, String>> lostUpdatePattern_15 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("WW","insert"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_16 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("WW","delete"), new Tuple<>("WW","update")));
        ArrayList<Tuple<String, String>> lostUpdatePattern_17 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("WW","delete"), new Tuple<>("WW","insert")));
        ArrayList<Tuple<String, String>> lostUpdatePattern_18 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("WW","delete"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_19 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("WW","update"), new Tuple<>("RW","update")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_20 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("WW","update"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_21 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("WW","update"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_22 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("WW","insert"), new Tuple<>("RW","update")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_23 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("WW","insert"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_24 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("WW","insert"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_25 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("WW","delete"), new Tuple<>("RW","update")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_26 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("WW","delete"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_27 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("WW","delete"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_28 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("WW","update"), new Tuple<>("RW","update")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_29 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("WW","update"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_30 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("WW","update"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_31 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("WW","insert"), new Tuple<>("RW","update")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_32 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("WW","insert"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_33 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("WW","insert"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_34 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("WW","delete"), new Tuple<>("RW","update")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_35 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("WW","delete"), new Tuple<>("WW","insert")));
		ArrayList<Tuple<String, String>> lostUpdatePattern_36 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("WW","delete"), new Tuple<>("WW","delete")));
		ArrayList<Tuple<String, String>> readSkewPattern_1 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("WR","update"), new Tuple<>("X","select"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> readSkewPattern_2 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("WR","insert"), new Tuple<>("X","select"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> readSkewPattern_3 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("WR","delete"), new Tuple<>("X","select"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> readSkewPattern_4 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("WR","update"), new Tuple<>("X","select"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> readSkewPattern_5 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("WR","insert"), new Tuple<>("X","select"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> readSkewPattern_6 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","insert"), new Tuple<>("WR","delete"), new Tuple<>("X","select"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> readSkewPattern_7 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("WR","update"), new Tuple<>("X","select"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> readSkewPattern_8 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("WR","insert"), new Tuple<>("X","select"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> readSkewPattern_9 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","delete"), new Tuple<>("WR","delete"), new Tuple<>("X","select"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> readSkewPattern_10 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","select"), new Tuple<>("RW","select"), new Tuple<>("X","update"), new Tuple<>("WR","update")));
		ArrayList<Tuple<String, String>> readSkewPattern_11 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","select"), new Tuple<>("RW","select"), new Tuple<>("X","update"), new Tuple<>("WR","insert")));
		ArrayList<Tuple<String, String>> readSkewPattern_12 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","select"), new Tuple<>("RW","select"), new Tuple<>("X","update"), new Tuple<>("WR","delete")));
		ArrayList<Tuple<String, String>> readSkewPattern_13 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","select"), new Tuple<>("RW","select"), new Tuple<>("X","insert"), new Tuple<>("WR","update")));
		ArrayList<Tuple<String, String>> readSkewPattern_14 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","select"), new Tuple<>("RW","select"), new Tuple<>("X","insert"), new Tuple<>("WR","insert")));
		ArrayList<Tuple<String, String>> readSkewPattern_15 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","select"), new Tuple<>("RW","select"), new Tuple<>("X","insert"), new Tuple<>("WR","delete")));
		ArrayList<Tuple<String, String>> readSkewPattern_16 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","select"), new Tuple<>("RW","select"), new Tuple<>("X","delete"), new Tuple<>("WR","update")));
		ArrayList<Tuple<String, String>> readSkewPattern_17 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","select"), new Tuple<>("RW","select"), new Tuple<>("X","delete"), new Tuple<>("WR","insert")));
		ArrayList<Tuple<String, String>> readSkewPattern_18 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","select"), new Tuple<>("RW","select"), new Tuple<>("X","delete"), new Tuple<>("WR","delete")));
        ArrayList<Tuple<String, String>> multiPattern_1 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("WR","update"), new Tuple<>("RW","select"), new Tuple<>("WW","update")));
        ArrayList<Tuple<String, String>> multiPattern_2 = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","update"), new Tuple<>("WW","update"), new Tuple<>("WR","update"), new Tuple<>("RW","select")));
		ArrayList<Tuple<String, String>> extensionPattern = new ArrayList<Tuple<String, String>>(Arrays.asList(new Tuple<>("X","extension")));
		anmlsTypesPatterns.put(dirtyReadPattern_1, "Dirty Reads");
		anmlsTypesPatterns.put(nonRepeatableReadPattern_1, "Non-Repeatable Reads");
		anmlsTypesPatterns.put(phantomReadPattern_1, "Phantom Reads");
		anmlsTypesPatterns.put(phantomReadPattern_2, "Phantom Reads");
		anmlsTypesPatterns.put(phantomReadPattern_3, "Phantom Reads");
		anmlsTypesPatterns.put(phantomReadPattern_4, "Phantom Reads");
		anmlsTypesPatterns.put(dirtyWritePattern_1, "Dirty Writes");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_1, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_2, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_3, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_4, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_5, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_6, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_7, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_8, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_9, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_10, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_11, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_12, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_13, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_14, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_15, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_16, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_17, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_18, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_19, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_20, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_21, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_22, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_23, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_24, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_25, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_26, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_27, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_28, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_29, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_30, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_31, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_32, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_33, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_34, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_35, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdateWriteSkew_36, "Lost Updates/Write Skews");
		anmlsTypesPatterns.put(lostUpdatePattern_1, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_2, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_3, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_4, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_5, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_6, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_7, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_8, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_9, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_10, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_11, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_12, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_13, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_14, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_15, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_16, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_17, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_18, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_19, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_20, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_21, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_22, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_23, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_24, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_25, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_26, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_27, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_28, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_29, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_30, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_31, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_32, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_33, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_34, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_35, "Lost Updates");
		anmlsTypesPatterns.put(lostUpdatePattern_36, "Lost Updates");
		anmlsTypesPatterns.put(readSkewPattern_1, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_2, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_3, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_4, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_5, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_6, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_7, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_8, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_9, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_10, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_11, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_12, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_13, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_14, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_15, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_16, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_17, "Read Skews");
		anmlsTypesPatterns.put(readSkewPattern_18, "Read Skews");
		anmlsTypesPatterns.put(multiPattern_1, "Multis");
		anmlsTypesPatterns.put(multiPattern_2, "Multis");
		anmlsTypesPatterns.put(extensionPattern, "Extensions");
		return anmlsTypesPatterns;
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
			if(anmlName.equals("Lost Updates/Write Skews") || anmlName.equals("Non-Repeatable Reads"))
				System.out.println("+++ " + anmlName + " found:\t" + anmlsCounters.get(anmlName));
			else if(!anmlName.equals("Extensions") && !anmlName.equals("Multis"))
				System.out.println("+++ " + anmlName + " found:\t\t" + anmlsCounters.get(anmlName));
		}
		System.out.println("+++ Multi anomalies found:\t\t" + anmlsCounters.get("Multis"));
		System.out.println("+++ Extensions found:\t\t" + anmlsCounters.get("Extensions"));
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
