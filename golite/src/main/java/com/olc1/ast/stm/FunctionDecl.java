package com.olc1.ast.stm;

import java.util.ArrayList;
import java.util.List;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class FunctionDecl implements ASTNODE {
    private final String name;
    private final String returnType;
    private final List<FunctionParam> params;
    private final ASTNODE body;
    private final int line;
    private final int column;

    public FunctionDecl(String name, String returnType, ASTNODE body, int line, int column) {
        this(name, returnType, new ArrayList<>(), body, line, column);
    }

    public FunctionDecl(String name, String returnType, List<FunctionParam> params, ASTNODE body, int line, int column) {
        this.name = name;
        this.returnType = returnType;
        this.params = params;
        this.body = body;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final String name;
        public final String returnType;
        public final List<FunctionParam> params;
        public final ASTNODE body;
        public final int line;
        public final int column;

        public Context(FunctionDecl node) {
            this.name = node.name;
            this.returnType = node.returnType;
            this.params = node.params;
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