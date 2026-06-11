package com.olc1.ast.exp;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class EmbeddedFunction implements ASTNODE {
    private final String functionName;
    private final ASTNODE expression;
    private final int line;
    private final int column;

    public EmbeddedFunction(String functionName, ASTNODE expression, int line, int column) {
        this.functionName = functionName;
        this.expression = expression;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final String functionName;
        public final ASTNODE expression;
        public final int line;
        public final int column;

        public Context(EmbeddedFunction node) {
            this.functionName = node.functionName;
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