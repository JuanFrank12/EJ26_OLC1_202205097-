package com.olc1.visitor;

import com.olc1.ast.exp.Add;
import com.olc1.ast.exp.AllLocate;
import com.olc1.ast.exp.BoolLiteral;
import com.olc1.ast.exp.Decimal;
import com.olc1.ast.exp.Div;
import com.olc1.ast.exp.Integers;
import com.olc1.ast.exp.LowerTag;
import com.olc1.ast.exp.Mul;
import com.olc1.ast.exp.Negate;
import com.olc1.ast.exp.Paren;
import com.olc1.ast.exp.StringLiteral;
import com.olc1.ast.exp.Sub;
import com.olc1.ast.exp.VarRef;
import com.olc1.ast.stm.Assign;
import com.olc1.ast.stm.BreakStm;
import com.olc1.ast.stm.IfNode;
import com.olc1.ast.stm.Imprimir;
import com.olc1.ast.stm.Statments;
import com.olc1.ast.stm.WhileFor;
import com.olc1.ast.stm.ElifNode;
import com.olc1.ast.stm.ElifNodes;
import com.olc1.ast.exp.NilLiteral;
import com.olc1.ast.stm.Block;
import com.olc1.ast.exp.Mod;
import com.olc1.ast.exp.Compare;
import com.olc1.ast.exp.Logical;
import com.olc1.ast.stm.ContinueStm;
import com.olc1.ast.stm.VarDecl;
import com.olc1.ast.exp.RuneLiteral;
import com.olc1.ast.exp.EmbeddedFunction;









public interface Visitor<T> {
    T visit(Integers.Context ctx);
    T visit(Decimal.Context ctx);
    T visit(Paren.Context ctx);
    T visit(Add.Context ctx);
    T visit(Sub.Context ctx);
    T visit(Mul.Context ctx);
    T visit(Div.Context ctx);
    T visit(Negate.Context ctx);
    T visit(BoolLiteral.Context ctx);
    T visit(StringLiteral.Context ctx);
    T visit(VarRef.Context ctx);
    T visit(Imprimir.Context ctx);
    T visit(Assign.Context ctx);
    T visit(IfNode.Context ctx);
    T visit(Statments.Context ctx);
    T visit(ElifNode.Context ctx);
    T visit(ElifNodes.Context ctx);
    T visit(AllLocate.Context ctx);
    T visit(LowerTag.Context ctx);
    T visit(BreakStm.Context ctx);
    T visit(WhileFor.Context ctx);
    T visit(NilLiteral.Context ctx);
    T visit(Block.Context ctx);
    T visit(Mod.Context ctx);
    T visit(Compare.Context ctx);
    T visit(Logical.Context ctx);
    T visit(ContinueStm.Context ctx);
    T visit(VarDecl.Context ctx);
    T visit(RuneLiteral.Context ctx);
    T visit(EmbeddedFunction.Context ctx);

}