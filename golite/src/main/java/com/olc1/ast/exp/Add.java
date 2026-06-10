package com.olc1.ast.exp;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class Add implements ASTNODE {
    private final ASTNODE left;
    private final ASTNODE right;

    public Add(ASTNODE left, ASTNODE right) {
        this.left = left;
        this.right = right;
    }

    public static class Context {
        public final ASTNODE left;
        public final ASTNODE right;

        public Context(Add node) {
            this.left = node.left;
            this.right = node.right;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}