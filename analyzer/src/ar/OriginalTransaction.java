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
	private Map<String, ParamValExp> params;
	private Map<Value, Expression> exps;

	public String getName() {
		return this.name;
	}

	public OriginalTransaction(String name) {
		this.name = name;
		this.stmts = new ArrayList<Statement>();
		this.params = new HashMap<String, ParamValExp>();
	}

	public void addStmt(Statement stmt) {
		this.stmts.add(stmt);
	}

	public void addParam(String l, ParamValExp p) {
		this.params.put(l, p);
	}

	public void addAllStmts(List<Statement> stmts) {
		this.stmts.addAll(stmts);
	}

	public void setTypes() {
		int selectCount = 0;
		int insertCount = 0;
		int deleteCount = 0;
		int updateCount = 0;
		int seq = 0;
		for (Statement s : stmts)
			try {
				InvokeStmt is = (InvokeStmt) s;
				if (is.getQuery().getText().toLowerCase().contains("select"))
					is.setType(new SqlStmtType(name, "select", ++selectCount, false, ++seq));
				else if (is.getQuery().getText().toLowerCase().contains("insert"))
					is.setType(new SqlStmtType(name, "insert", ++insertCount, true, ++seq));
				else if (is.getQuery().getText().toLowerCase().contains("update"))
					is.setType(new SqlStmtType(name, "update", ++updateCount, true, ++seq));
				else if (is.getQuery().getText().toLowerCase().contains("delete"))
					is.setType(new SqlStmtType(name, "delete", ++deleteCount, true, ++seq));

			} catch (Exception e) {
			}
	}

	public List<VarExp> getAllLhsVars() {
		List<VarExp> result = new ArrayList<VarExp>();
		for (Statement s : this.stmts)
			try {
				AssignmentStmt as = (AssignmentStmt) s;
				result.add(as.getLhs());
			} catch (Exception e) {
			}
		return result;

	}

	public void setExps(Map<Value, Expression> exps) {
		this.exps = exps;
	}

	public ArrayList<Statement> getStmts() {
		return this.stmts;
	}

	public Map<String, ParamValExp> getParams() {
		return this.params;
	}

	public Map<Value, Expression> getAllExps() {
		return this.exps;
	}

	public void printOrigTxn() {
		String paramList = " (";
		int iter = 0;
		for (String s : params.keySet()) {
			paramList += (s + ":" + params.get(s).getType());
			if (iter++ < params.size() - 1)
				paramList += ",";
		}

		paramList += ")";
		System.out.println("\nORIG_TXN_" + name + paramList);
		for (Statement stmt : stmts)
			try {
				System.out.println(" ++ " + ((InvokeStmt) stmt).toString());
			} catch (ClassCastException e) {
				System.out.println(" ++ UNEXPECTED -> cast to (InvokeStmt) failed ...");
			}
		
		System.out.println("----------------------------------------------------------------");

	}

}
