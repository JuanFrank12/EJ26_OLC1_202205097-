package com.olc1.ast.stm;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class ForRange implements ASTNODE {
    private final String indexName;
    private final String valueName;
    private final ASTNODE iterable;
    private final ASTNODE body;
    private final int line;
    private final int column;

    public ForRange(String indexName, String valueName, ASTNODE iterable, ASTNODE body, int line, int column) {
        this.indexName = indexName;
        this.valueName = valueName;
        this.iterable = iterable;
        this.body = body;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final String indexName;
        public final String valueName;
        public final ASTNODE iterable;
        public final ASTNODE body;
        public final int line;
        public final int column;

        public Context(ForRange node) {
            this.indexName = node.indexName;
            this.valueName = node.valueName;
            this.iterable = node.iterable;
            this.body = node.body;
            this.line = node.line;
            this.column = node.column;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}