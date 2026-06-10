package com.olc1.ast.stm;
import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;



public class ElifNode implements ASTNODE {
    // Se encarga de reconocer: else if (condicion) { statements }

    private final ASTNODE condition;
    private final ASTNODE body; // statements
    public Context ctx;

    public ElifNode(ASTNODE condition, ASTNODE body) {
        this.condition = condition;
        this.body = body;
    }

    public static class Context {
        public final ASTNODE condition;
        public final ASTNODE body;

        public Context(ElifNode node) {
            this.condition = node.condition;
            this.body = node.body;
            node.ctx = this;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}