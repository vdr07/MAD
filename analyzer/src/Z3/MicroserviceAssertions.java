package Z3;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Quantifier;

public class MicroserviceAssertions {

	Context ctx;
	DeclaredObjects objs;
	Expr o1, o2, o3;

	public MicroserviceAssertions(Context ctx, DeclaredObjects objs) {
		this.ctx = ctx;
		this.objs = objs;
		o1 = ctx.mkFreshConst("o", objs.getSort("O"));
		o2 = ctx.mkFreshConst("o", objs.getSort("O"));
		o3 = ctx.mkFreshConst("o", objs.getSort("O"));
	}

//	public BoolExpr mk_ar_t() {
//		BoolExpr lhs = (BoolExpr) ctx.mkApp(objs.getfuncs("ar"), o1, o2);
//		BoolExpr rhs = (BoolExpr) ctx.mkApp(objs.getfuncs("ar_t"), o1, o2);
//		BoolExpr body = ctx.mkImplies(lhs, rhs);
//		Quantifier x = ctx.mkForall(new Expr[]{o1, o2}, body, 1, null, null, null, null);
//		BoolExpr lhs2 = ctx.mkAnd((BoolExpr) ctx.mkApp(objs.getfuncs("ar_t"), o1, o2), (BoolExpr) ctx.mkApp(objs.getfuncs("ar_t"), o2, o3));
//		BoolExpr rhs2 = (BoolExpr) ctx.mkApp(objs.getfuncs("ar_t"), o1, o3);
//		BoolExpr body2 = ctx.mkImplies(lhs2, rhs2);
//		Quantifier y = ctx.mkForall(new Expr[]{o1, o2, o3}, body2, 1, null, null, null, null);
//		return ctx.mkAnd(x, y);
//	}

	public BoolExpr mkHappensAfter(String stmt1, String stmt2) {
		BoolExpr part1 = ctx.mkEq(
				ctx.mkApp(objs.getfuncs("otype"), o1),
				ctx.mkApp(objs.getConstructor("OType", stmt1))
		);
		BoolExpr part2 = ctx.mkEq(
				ctx.mkApp(objs.getfuncs("otype"), o2),
				ctx.mkApp(objs.getConstructor("OType", stmt2))
		);
		BoolExpr part3 = ctx.mkNot(ctx.mkEq(o1, o2));
		BoolExpr body = ctx.mkImplies(
				ctx.mkAnd(part1, part2, part3),
				(BoolExpr) ctx.mkApp(objs.getfuncs("ar"), o1, o2)
		);
		return ctx.mkForall(new Expr[]{o1, o2}, body, 1, null, null, null, null);
	}
}
