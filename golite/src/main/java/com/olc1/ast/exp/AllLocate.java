package com.olc1.ast.exp;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;


public class AllLocate implements ASTNODE{
    private final String id;
    private final ASTNODE expression;
    private int line, column;

    public AllLocate(String id, ASTNODE expression, int line, int column){
        this.id= id;
        this.expression = expression;
        this.line = line;
        this.column = column;
    }

    public static class Context  {
        public final String id;
        public final ASTNODE expression;
        public int line, column;

        public Context(AllLocate node){
            this.id = node.id;
            this.expression = node.expression;
            this.line = node.line;
            this.column = node.column;

        }
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }

}


