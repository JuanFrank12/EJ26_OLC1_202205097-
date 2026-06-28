package com.olc1.ast.exp;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class SliceAccess implements ASTNODE {
    private final ASTNODE slice;
    private final ASTNODE index;
    private final int line;
    private final int column;

    public SliceAccess(ASTNODE slice, ASTNODE index, int line, int column) {
        this.slice = slice;
        this.index = index;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final ASTNODE slice;
        public final ASTNODE index;
        public final int line;
        public final int column;

        public Context(SliceAccess node) {
            this.slice = node.slice;
            this.index = node.index;
            this.line = node.line;
            this.column = node.column;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}