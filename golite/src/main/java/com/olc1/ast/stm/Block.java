package com.olc1.ast.stm;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class Block implements ASTNODE {
    private final ASTNODE body;

    public Block(ASTNODE body) {
        this.body = body;
    }

    public static class Context {
        public final ASTNODE body;

        public Context(Block node) {
            this.body = node.body;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}