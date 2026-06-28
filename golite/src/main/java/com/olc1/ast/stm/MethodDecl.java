package com.olc1.ast.stm;

import java.util.ArrayList;
import java.util.List;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class MethodDecl implements ASTNODE {
    private final String receiverName;
    private final String receiverType;
    private final String methodName;
    private final String returnType;
    private final List<FunctionParam> params;
    private final ASTNODE body;
    private final int line;
    private final int column;

    public MethodDecl(
            String receiverName,
            String receiverType,
            String methodName,
            String returnType,
            ASTNODE body,
            int line,
            int column
    ) {
        this(receiverName, receiverType, methodName, returnType, new ArrayList<>(), body, line, column);
    }

    public MethodDecl(
            String receiverName,
            String receiverType,
            String methodName,
            String returnType,
            List<FunctionParam> params,
            ASTNODE body,
            int line,
            int column
    ) {
        this.receiverName = receiverName;
        this.receiverType = receiverType;
        this.methodName = methodName;
        this.returnType = returnType;
        this.params = params;
        this.body = body;
        this.line = line;
        this.column = column;
    }

    public static class Context {
        public final String receiverName;
        public final String receiverType;
        public final String methodName;
        public final String returnType;
        public final List<FunctionParam> params;
        public final ASTNODE body;
        public final int line;
        public final int column;

        public Context(MethodDecl node) {
            this.receiverName = node.receiverName;
            this.receiverType = node.receiverType;
            this.methodName = node.methodName;
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