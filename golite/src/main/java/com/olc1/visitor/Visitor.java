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
import com.olc1.ast.stm.ForRange;
import com.olc1.ast.exp.NilLiteral;
import com.olc1.ast.stm.Block;
import com.olc1.ast.exp.Mod;
import com.olc1.ast.exp.Compare;
import com.olc1.ast.exp.Logical;
import com.olc1.ast.stm.ContinueStm;
import com.olc1.ast.stm.VarDecl;
import com.olc1.ast.exp.RuneLiteral;
import com.olc1.ast.exp.EmbeddedFunction;
import com.olc1.ast.stm.MainFunction;
import com.olc1.ast.stm.SwitchNode;
import com.olc1.ast.stm.ReturnStm;
import com.olc1.ast.stm.FunctionDecl;
import com.olc1.ast.exp.FunctionCall;
import com.olc1.ast.exp.SliceLiteral;
import com.olc1.ast.exp.SliceAccess;
import com.olc1.ast.stm.SliceAssign;
import com.olc1.ast.exp.AppendFunction;
import com.olc1.ast.exp.SlicesIndexFunction;
import com.olc1.ast.exp.StringsJoinFunction;
import com.olc1.ast.stm.StructTypeDecl;
import com.olc1.ast.exp.StructLiteral;
import com.olc1.ast.exp.StructAccess;
import com.olc1.ast.stm.StructAssign;
import com.olc1.ast.stm.MethodDecl;
import com.olc1.ast.exp.MethodCall;
import com.olc1.ast.stm.ForRange;












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
    T visit(MainFunction.Context ctx);
    T visit(SwitchNode.Context ctx);
    T visit(ReturnStm.Context ctx);
    T visit(FunctionDecl.Context ctx);
    T visit(FunctionCall.Context ctx);
    T visit(SliceLiteral.Context ctx);
    T visit(SliceAccess.Context ctx);
    T visit(SliceAssign.Context ctx);
    T visit(AppendFunction.Context ctx);
    T visit(SlicesIndexFunction.Context ctx);
    T visit(StringsJoinFunction.Context ctx);
    T visit(StructTypeDecl.Context ctx);
    T visit(StructLiteral.Context ctx);
    T visit(StructAccess.Context ctx);
    T visit(StructAssign.Context ctx);
    T visit(MethodDecl.Context ctx);
    T visit(MethodCall.Context ctx);
    T visit(ForRange.Context ctx);

}