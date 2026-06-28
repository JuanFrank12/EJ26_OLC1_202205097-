package com.olc1.ast.stm;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class StructAssign implements ASTNODE {
    private final ASTNODE struct;
    private final String fieldName;
    private final ASTNODE value;
    private final int line;
    private final int column;

    public StructAssign(ASTNODE struct, String fieldName, ASTNODE value, int line, int column) {
        this.struct = struct;
        this.fieldName = fieldName;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final ASTNODE struct;
        public final String fieldName;
        public final ASTNODE value;
        public final int line;
        public final int column;

        public Context(StructAssign node) {
            this.struct = node.struct;
            this.fieldName = node.fieldName;
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