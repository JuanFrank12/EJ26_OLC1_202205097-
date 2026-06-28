package com.olc1.ast.stm;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class ReturnStm implements ASTNODE {
    private final ASTNODE expression;
    private final int line;
    private final int column;

    public ReturnStm(ASTNODE expression, int line, int column) {
        this.expression = expression;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final ASTNODE expression;
        public final int line;
        public final int column;

        public Context(ReturnStm node) {
            this.expression = node.expression;
            this.line = node.line;
            this.column = node.column;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}