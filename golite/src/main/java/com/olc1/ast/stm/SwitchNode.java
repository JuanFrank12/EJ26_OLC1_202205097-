package com.olc1.ast.stm;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class SwitchNode implements ASTNODE {
    private final ASTNODE expression;
    private final CaseNodes cases;
    private final ASTNODE defaultBody;
    private final int line;
    private final int column;

    public SwitchNode(ASTNODE expression, CaseNodes cases, ASTNODE defaultBody, int line, int column) {
        this.expression = expression;
        this.cases = cases;
        this.defaultBody = defaultBody;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final ASTNODE expression;
        public final CaseNodes cases;
        public final ASTNODE defaultBody;
        public final int line;
        public final int column;

        public Context(SwitchNode node) {
            this.expression = node.expression;
            this.cases = node.cases;
            this.defaultBody = node.defaultBody;
            this.line = node.line;
            this.column = node.column;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}