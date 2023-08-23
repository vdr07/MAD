package fec;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import exceptions.SqlTypeNotFoundException;
import exceptions.UnknownUnitException;
import ar.Application;
import ar.Transaction;
import ar.OriginalTransaction;
import ar.Type;
import ar.expression.vals.ParamValExp;
import ar.ddl.Table;
import ar.statement.Statement;
import ar.statement.InvokeStmt;
import cons.ConstantArgs;
import soot.Body;
import soot.Local;
import soot.Scene;
import soot.Transformer;
import soot.Unit;
import soot.Value;
import soot.tagkit.*;

public class GimpToAppOne extends GimpToApp {
	private static final Logger LOG = LogManager.getLogger(Transformer.class);

	public GimpToAppOne(Scene v2, ArrayList<Body> bodies, ArrayList<Table> tables) {
		super(v2, bodies, tables);
	}

	@SuppressWarnings("unchecked")
	public Application transform() throws UnknownUnitException {
		Application app = new Application();

		Map<String, String> entitiesMicroservicesMap = new HashMap<String, String>();
		try {
			Object obj = new JSONParser().parse(new FileReader("decomposition.json"));
			JSONObject jo = (JSONObject) obj;
			for (Object key : jo.keySet()) {
				String keyStr = (String) key;

				JSONArray microserviceEntities = (JSONArray) jo.get(key);
				microserviceEntities.forEach(entity -> {
					String entityStr = (String) entity;
					entitiesMicroservicesMap.put(entityStr, keyStr);
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Body b : bodies) {
			if (!b.getMethod().getName().contains("init")
					&& !ConstantArgs._EXCLUDED_TXNS.contains(b.getMethod().getName())) {
				OriginalTransaction origTxn = extractOrigTxn(b, app, entitiesMicroservicesMap);
				LOG.info("Transaction <<" + b.getMethod().getName() + ">> compiled to AR");

				if (origTxn != null)
					app.addOrigTxn(origTxn);
			}
		}
		LOG.info("AR application successfully generated");
		return app;
	}

	private OriginalTransaction extractOrigTxn(Body b, Application app, Map<String, String> entitiesMicroservicesMap) throws UnknownUnitException {

		if (ConstantArgs.DEBUG_MODE)
			super.printGimpBody(b);
		String name = b.getMethod().getName();
		OriginalTransaction origTxn = new OriginalTransaction(name);
		UnitHandler unitHandler = new UnitHandler(b, super.tables);
		// INTERNAL ANALYSIS
		// Parameter extraction
		unitHandler.extractParams();
		LOG.info("Original Transaction <<" + name + ">> parameters extracted");

		for (Local l : unitHandler.data.getParams().keySet()) {
			Type t = Type.INT; // just to instantiate it, needed for calling the typing function
			Value v = unitHandler.data.getParams().get(l);

			try {
				ParamValExp exp = (ParamValExp) new ParamValExp(l.toString(), t.fromJavaTypes(v), "to-do");
				unitHandler.data.addExp(l, exp);
			} catch (SqlTypeNotFoundException e) {
				e.printStackTrace();
			}
		}

		unitHandler.InitialAnalysis();
		LOG.info("Initial analysis done");
		unitHandler.extractStatements();
		LOG.info("Statements extracted");
		unitHandler.finalAnalysis();
		LOG.info("Final analysis done");
		unitHandler.finalizeStatements();
		LOG.info("Statements finalized");

		// Considering that indexes start at 0 and the first subtxn will be at 0
		int subTransactionsIdx = -1;
		String currentMicroservice = "";
		List<Transaction> subTxns = new ArrayList<>();
		// craft the output transaction from the extracted data
		for (Statement s : unitHandler.data.getStmts()) {
			String entityName = ((InvokeStmt) s).getQuery().getTable().getName();
			if (!currentMicroservice.equals(entitiesMicroservicesMap.get(entityName))) {
				currentMicroservice = entitiesMicroservicesMap.get(entityName);
				Transaction newSubTxn = new Transaction(name + "_" + (subTransactionsIdx+1));
				newSubTxn.setOriginalTransaction(name);
				newSubTxn.setMicroservice(currentMicroservice);
				newSubTxn.addStmt(s);
				subTxns.add(newSubTxn);
				subTransactionsIdx++;
			} else {
				subTxns.get(subTransactionsIdx).addStmt(s);
			}
			origTxn.addStmt(s);
			
		}

		for (Transaction subTxn : subTxns) {
			for (Local l : unitHandler.data.getParams().keySet()) {
				subTxn.addParam(l.toString(), (ParamValExp) unitHandler.data.getExp(l));
			}
			subTxn.setExps(unitHandler.data.getExps());
			subTxn.setTypes();

			app.addTxn(subTxn);
		}

		// if (ConstantArgs.DEBUG_MODE)
		// printExpressions(unitHandler);
		return origTxn;
	}

	// just a helping function for dev phase
	private void printExpressions(UnitHandler unitHandler) {
		if (ConstantArgs.DEBUG_MODE) {
			System.out.println("===== LOOPS");
			for (Unit x : unitHandler.data.units)
				if (unitHandler.data.getLoopNo(x) == -1)
					System.out.println("" + unitHandler.data.units.indexOf(x));
				else
					System.out.println(
							"__" + unitHandler.data.units.indexOf(x) + "(" + unitHandler.data.getLoopNo(x) + ")");
		}

		System.out.println("=============================");
		System.out.println("===	VARIABLES");
		for (Value x : unitHandler.data.getExps().keySet()) {
			System.out.println(x + " := " + unitHandler.data.getExps().get(x));
		}
	}

}
