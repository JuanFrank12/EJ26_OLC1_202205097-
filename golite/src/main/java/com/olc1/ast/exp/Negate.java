package com.olc1.ast.exp;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class Negate implements ASTNODE {
    private final ASTNODE expression;

    public Negate(ASTNODE expression) {
        this.expression = expression;
    }

    public static class Context {
        public final ASTNODE expression;

        public Context(Negate node) {
            this.expression = node.expression;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}