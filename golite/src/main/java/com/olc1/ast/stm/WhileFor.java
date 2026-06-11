package com.olc1.ast.stm;


import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class WhileFor implements ASTNODE {

    private final ASTNODE init;
    private final ASTNODE condition;
    private final ASTNODE update;
    private final ASTNODE body;

    // for condicion { ... }
    public WhileFor(ASTNODE condition, ASTNODE body) {
        this.init = null;
        this.condition = condition;
        this.update = null;
        this.body = body;
    }

    // for init; condicion; update { ... }
    public WhileFor(ASTNODE init, ASTNODE condition, ASTNODE update, ASTNODE body) {
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    public static class Context {
        public final ASTNODE init;
        public final ASTNODE condition;
        public final ASTNODE update;
        public final ASTNODE body;

        public Context(WhileFor node) {
            this.init = node.init;
            this.condition = node.condition;
            this.update = node.update;
            this.body = node.body;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}