package com.olc1.ast.stm;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;


public class BreakStm implements ASTNODE{
    private int line, column;

    public BreakStm(int line, int column){
        this.line = line;
        this.column = column;
    }

    public static class Context{
        public final int line, column;

        public Context(BreakStm node){
            this.line = node.line;
            this.column = node.column;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }

}
