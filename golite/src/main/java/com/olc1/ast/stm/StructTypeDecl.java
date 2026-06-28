package com.olc1.ast.stm;

import java.util.List;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class StructTypeDecl implements ASTNODE {
    private final String name;
    private final List<StructField> fields;
    private final int line;
    private final int column;

    public StructTypeDecl(String name, List<StructField> fields, int line, int column) {
        this.name = name;
        this.fields = fields;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final String name;
        public final List<StructField> fields;
        public final int line;
        public final int column;

        public Context(StructTypeDecl node) {
            this.name = node.name;
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