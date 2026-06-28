package com.olc1.ast.exp;

import java.util.List;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class StructLiteral implements ASTNODE {
    private final String structName;
    private final List<StructFieldInit> fields;
    private final int line;
    private final int column;

    public StructLiteral(String structName, List<StructFieldInit> fields, int line, int column) {
        this.structName = structName;
        this.fields = fields;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final String structName;
        public final List<StructFieldInit> fields;
        public final int line;
        public final int column;

        public Context(StructLiteral node) {
            this.structName = node.structName;
            this.fields = node.fields;
            this.line = node.line;
            this.column = node.column;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}