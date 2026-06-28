package com.olc1.ast.exp;

import java.util.ArrayList;
import java.util.List;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class FunctionCall implements ASTNODE {
    private final String name;
    private final List<ASTNODE> arguments;
    private final int line;
    private final int column;

    public FunctionCall(String name, int line, int column) {
        this(name, new ArrayList<>(), line, column);
    }

    public FunctionCall(String name, List<ASTNODE> arguments, int line, int column) {
        this.name = name;
        this.arguments = arguments;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final String name;
        public final List<ASTNODE> arguments;
        public final int line;
        public final int column;

        public Context(FunctionCall node) {
            this.name = node.name;
            this.arguments = node.arguments;
            this.line = node.line;
            this.column = node.column;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}