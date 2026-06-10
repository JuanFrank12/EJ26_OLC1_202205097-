package com.olc1.ast.stm;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;


public class Assign implements ASTNODE {
    private final String name;
    private final ASTNODE value;
    private final int line;
    private final int column;

    public Assign(String name, ASTNODE value, int line, int column) {
        this.name = name;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final String name;
        public final ASTNODE value;
        public final int line;
        public final int column;

        public Context(Assign node) {
            this.name = node.name;
            this.value = node.value;
            this.line = node.line;
            this.column = node.column;
        }
    }

    @Override
    public <T> T accept(Visitor<T> Visitor) {
        return Visitor.visit(new Context(this));
    }
}