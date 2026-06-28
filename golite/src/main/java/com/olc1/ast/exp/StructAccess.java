package com.olc1.ast.exp;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class StructAccess implements ASTNODE {
    private final ASTNODE struct;
    private final String fieldName;
    private final int line;
    private final int column;

    public StructAccess(ASTNODE struct, String fieldName, int line, int column) {
        this.struct = struct;
        this.fieldName = fieldName;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final ASTNODE struct;
        public final String fieldName;
        public final int line;
        public final int column;

        public Context(StructAccess node) {
            this.struct = node.struct;
            this.fieldName = node.fieldName;
            this.line = node.line;
            this.column = node.column;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}