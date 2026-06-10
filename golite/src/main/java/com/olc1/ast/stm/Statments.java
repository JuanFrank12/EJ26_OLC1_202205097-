package com.olc1.ast.stm;

import java.util.ArrayList;
import java.util.List;

import com.olc1.ast.ASTNODE;

public class Statments implements ASTNODE {
    private final List<ASTNODE> statements;

    public Statments(ASTNODE statement) {
         this.statements = new ArrayList<>();
         this.statements.add(statement);
    }

    public void add(ASTNODE statement) {
        if(statement != null){
            this.statements.add(statement);
        };
    }

    public class Context {
        public final List<ASTNODE> statements;

        public Context(ASTNODE node) {
            this.statements = ((Statments) node).statements;
        }
    }

    @Override
    public <T> T accept(com.olc1.visitor.Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}