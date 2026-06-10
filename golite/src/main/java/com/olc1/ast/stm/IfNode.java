package com.olc1.ast.stm;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class IfNode implements ASTNODE {
    private final ASTNODE condition;
    private final ASTNODE body;
    private final ElifNodes elifList;

    public IfNode(ASTNODE condition, ASTNODE body) {
        this.condition = condition;
        this.body = body;
        this.elifList = null;
    }

    public IfNode(ASTNODE condition, ASTNODE body , ElifNodes elifList) {
        this.condition = condition;
        this.body = body;
        this.elifList = elifList;
    }

    public static class Context {
        public final ASTNODE condition;
        public final ASTNODE body;
        public final ElifNodes elifList;
        
        public Context(IfNode node) {
            this.condition = node.condition;
            this.body = node.body;
            this.elifList = node.elifList;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}