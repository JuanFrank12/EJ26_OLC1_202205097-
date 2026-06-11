package com.olc1.ast.stm;

import java.util.ArrayList;
import java.util.List;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class Imprimir implements ASTNODE {
    private final List<ASTNODE> expressions;

    public Imprimir(ASTNODE expression) {
        this.expressions = new ArrayList<>();
        this.expressions.add(expression);
    }

    public Imprimir(List<ASTNODE> expressions) {
        this.expressions = expressions;
    }

    public static class Context {
        public final List<ASTNODE> expressions;

        public Context(Imprimir node) {
            this.expressions = node.expressions;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}