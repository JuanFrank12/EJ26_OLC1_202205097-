package com.olc1.visitor;



import com.olc1.ast.exp.Add;
import com.olc1.ast.exp.BoolLiteral;
import com.olc1.ast.exp.Decimal;
import com.olc1.ast.exp.Div;
import com.olc1.ast.exp.Integers;
import com.olc1.ast.exp.Mul;
import com.olc1.ast.exp.Negate;
import com.olc1.ast.exp.Paren;
import com.olc1.ast.exp.StringLiteral;
import com.olc1.ast.exp.Sub;
import com.olc1.ast.exp.VarRef;
import com.olc1.ast.stm.Assign;
import com.olc1.ast.stm.IfNode;
import com.olc1.ast.stm.Imprimir;
import com.olc1.ast.stm.Statments;


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
}