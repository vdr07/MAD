package fec;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exceptions.SqlTypeNotFoundException;
import exceptions.UnknownUnitException;
import ar.Application;
import ar.Transaction;
import ar.Type;
import ar.expression.vals.ParamValExp;
import ar.ddl.Table;
import ar.statement.Statement;
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

	public Application transform() throws UnknownUnitException {
		Application app = new Application();
		for (Body b : bodies) {
			if (!b.getMethod().getName().contains("init")
					&& !ConstantArgs._EXCLUDED_TXNS.contains(b.getMethod().getName())) {
				Transaction txn = extractTxn(b, app);
				LOG.info("Transaction <<" + b.getMethod().getName() + ">> compiled to AR");

				if (txn != null)
					app.addTxn(txn);
			}
		}
		LOG.info("AR application successfully generated");
		return app;
	}

	private Transaction extractTxn(Body b, Application app) throws UnknownUnitException {

		if (ConstantArgs.DEBUG_MODE)
			super.printGimpBody(b);
		String name = b.getMethod().getName();
		Transaction txn = new Transaction(name);
		UnitHandler unitHandler = new UnitHandler(b, super.tables);
		// INTERNAL ANALYSIS
		// Parameter extraction
		unitHandler.extractParams();
		LOG.info("Transaction <<" + name + ">> parameters extracted");
		for (Local l : unitHandler.data.getParams().keySet()) {
			Type t = Type.INT; // just to instantiate it, needed for calling the typing function
			Value v = unitHandler.data.getParams().get(l);

			try {
				ParamValExp exp = (ParamValExp) new ParamValExp(l.toString(), t.fromJavaTypes(v), "to-do");
				txn.addParam(l.toString(), exp);
				// Also add it the unit data
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

		// craft the output transaction from the extracted data
		for (Statement s : unitHandler.data.getStmts()) {
			txn.addStmt(s);
		}
		txn.setExps(unitHandler.data.getExps());
		txn.setTypes();

		Tag tags = b.getMethod().getTag("VisibilityAnnotationTag");
		if (tags != null) {
			for (AnnotationTag tag : ((VisibilityAnnotationTag) tags).getAnnotations()) {
				if (Objects.equals(tag.getType(), "Lar/DependsOn;")) {
					Optional<String> dependency = tag.getElems().stream().filter((a) -> Objects.equals(a.getName(), "name")).
							map((x) -> ((AnnotationStringElem) x).getValue()).
							findFirst();
					dependency.ifPresent(s -> {
						LOG.info("Found dependency: {} depends on {}", name, s);
						//txn.addDependency(s);

						// Add the transaction statements to the dependant transaction that happens before
						Optional<Transaction> depTxn = app.getTxns().stream().filter((a) -> Objects.equals(a.getName(), s)).
							findFirst();
						depTxn.ifPresent(dt -> {
							for (Statement st : txn.getStmts()) {
								dt.addStmt(st);
							}
							dt.setTypes();	// Update the statements names to the dependant transaction
						});
					});
					return null;	// Discard the current transaction, only accounting for the dependant transaction
				} else if (Objects.equals(tag.getType(), "Lar/ChoppedTransaction;")) {
					Optional<String> originalTransaction = tag.getElems().stream().filter((a) -> Objects.equals(a.getName(), "originalTransaction")).
							map((x) -> ((AnnotationStringElem) x).getValue()).
							findFirst();
					originalTransaction.ifPresent(ot -> {
						LOG.info("Chopped transaction: {} belonged to transaction {}", name,ot);
						txn.setOriginalTransaction(ot);
					});

					Optional<String> microservice = tag.getElems().stream().filter((a) -> Objects.equals(a.getName(), "microservice")).
							map((x) -> ((AnnotationStringElem) x).getValue()).
							findFirst();
					microservice.ifPresent(m -> {
						LOG.info("Chopped transaction: {} executes on microservice {}", name, microservice);
						txn.setMicroservice(m);
					});
				}
			}
		}

		if(txn.getOriginalTransaction() == null) txn.setOriginalTransaction(name);

		// if (ConstantArgs.DEBUG_MODE)
		// printExpressions(unitHandler);
		return txn;
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
