package com.olc1.ast.exp;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class Logical implements ASTNODE {
    private final ASTNODE left;
    private final ASTNODE right;
    private final String operator;

    public Logical(ASTNODE left, ASTNODE right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public static class Context {
        public final ASTNODE left;
        public final ASTNODE right;
        public final String operator;

        public Context(Logical node) {
            this.left = node.left;
            this.right = node.right;
            this.operator = node.operator;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}