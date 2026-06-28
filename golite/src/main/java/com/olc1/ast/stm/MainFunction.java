package com.olc1.ast.stm;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class MainFunction implements ASTNODE {
    private final String name;
    private final ASTNODE body;
    private final int line;
    private final int column;

    public MainFunction(String name, ASTNODE body, int line, int column) {
        this.name = name;
        this.body = body;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final String name;
        public final ASTNODE body;
        public final int line;
        public final int column;

        public Context(MainFunction node) {
            this.name = node.name;
            this.body = node.body;
            this.line = node.line;
            this.column = node.column;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}