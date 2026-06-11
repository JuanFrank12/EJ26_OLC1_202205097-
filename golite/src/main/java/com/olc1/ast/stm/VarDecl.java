package com.olc1.ast.stm;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class VarDecl implements ASTNODE {
    private final String name;
    private final String type;
    private final ASTNODE value;
    private final int line;
    private final int column;

    public VarDecl(String name, String type, ASTNODE value, int line, int column) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final String name;
        public final String type;
        public final ASTNODE value;
        public final int line;
        public final int column;

        public Context(VarDecl node) {
            this.name = node.name;
            this.type = node.type;
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