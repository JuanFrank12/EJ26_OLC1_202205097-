package com.olc1.ast.exp;

import java.util.ArrayList;
import java.util.List;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class MethodCall implements ASTNODE {
    private final ASTNODE target;
    private final String methodName;
    private final List<ASTNODE> arguments;
    private final int line;
    private final int column;

    public MethodCall(ASTNODE target, String methodName, int line, int column) {
        this(target, methodName, new ArrayList<>(), line, column);
    }

    public MethodCall(ASTNODE target, String methodName, List<ASTNODE> arguments, int line, int column) {
        this.target = target;
        this.methodName = methodName;
        this.arguments = arguments;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final ASTNODE target;
        public final String methodName;
        public final List<ASTNODE> arguments;
        public final int line;
        public final int column;

        public Context(MethodCall node) {
            this.target = node.target;
            this.methodName = node.methodName;
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