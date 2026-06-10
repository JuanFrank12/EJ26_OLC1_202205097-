package com.olc1.ast.exp;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class Sub implements ASTNODE {
    private final ASTNODE left;
    private final ASTNODE right;

    public Sub(ASTNODE left, ASTNODE right) {
        this.left = left;
        this.right = right;
    }

    public static class Context {
        public final ASTNODE left;
        public final ASTNODE right;

        public Context(Sub node) {
            this.left = node.left;
            this.right = node.right;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}