package com.olc1.ast.stm;

import java.util.List;
import java.util.ArrayList;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.Visitor;

public class ElifNodes implements ASTNODE {
    // Lista de else if / else
    private final List<ElifNode> elifNodesList;

    public Context ctx;

    public ElifNodes(ElifNode first) {
        this.elifNodesList = new ArrayList<>();
        this.elifNodesList.add(first);
        this.ctx = null;
    }

    public void add(ElifNode node) {
        this.elifNodesList.add(node);
    }

    // ESTE ES EL GETTER NUEVO IMPORTANTE
    public List<ElifNode> getElifNodesList() {
        return this.elifNodesList;
    }

    public class Context {
        public final List<ElifNode> elifNodesList;

        public Context(ElifNodes node) {
            this.elifNodesList = node.elifNodesList;
            node.ctx = this;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(new Context(this));
    }
}