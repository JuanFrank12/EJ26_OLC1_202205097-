package com.olc1.ast.exp;

import java.util.List;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class SliceLiteral implements ASTNODE {
    private final String elementType;
    private final List<ASTNODE> values;
    private final int line;
    private final int column;

    public SliceLiteral(String elementType, List<ASTNODE> values, int line, int column) {
        this.elementType = elementType;
        this.values = values;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final String elementType;
        public final List<ASTNODE> values;
        public final int line;
        public final int column;

        public Context(SliceLiteral node) {
            this.elementType = node.elementType;
            this.values = node.values;
            this.line = node.line;
            this.column = node.column;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}