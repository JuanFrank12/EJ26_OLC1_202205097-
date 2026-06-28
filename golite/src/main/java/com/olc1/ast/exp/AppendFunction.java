package com.olc1.ast.exp;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class AppendFunction implements ASTNODE {
    private final ASTNODE slice;
    private final ASTNODE value;
    private final int line;
    private final int column;

    public AppendFunction(ASTNODE slice, ASTNODE value, int line, int column) {
        this.slice = slice;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final ASTNODE slice;
        public final ASTNODE value;
        public final int line;
        public final int column;

        public Context(AppendFunction node) {
            this.slice = node.slice;
            this.value = node.value;
            this.line = node.line;
            this.column = node.column;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}