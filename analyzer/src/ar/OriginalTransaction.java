package ar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ar.expression.Expression;
import ar.expression.vals.ParamValExp;
import ar.expression.vars.VarExp;
import ar.statement.AssignmentStmt;
import ar.statement.InvokeStmt;
import ar.statement.SqlStmtType;
import ar.statement.Statement;
import soot.Value;

public class OriginalTransaction {
	private String name;
	private ArrayList<Statement> stmts;

	public String getName() {
		return this.name;
	}

	public OriginalTransaction(String name) {
		this.name = name;
		this.stmts = new ArrayList<Statement>();
	}

	public void addStmt(Statement stmt) {
		this.stmts.add(stmt);
	}

	public void addAllStmts(List<Statement> stmts) {
		this.stmts.addAll(stmts);
	}

	public ArrayList<Statement> getStmts() {
		return this.stmts;
	}

	public void printOrigTxn() {
		int iter = 0;
		System.out.println("\nORIG_TXN_" + name);
		for (Statement stmt : stmts)
			try {
				System.out.println(" ++ " + ((InvokeStmt) stmt).toString());
			} catch (ClassCastException e) {
				System.out.println(" ++ UNEXPECTED -> cast to (InvokeStmt) failed ...");
			}
		
		System.out.println("----------------------------------------------------------------");

	}

}
