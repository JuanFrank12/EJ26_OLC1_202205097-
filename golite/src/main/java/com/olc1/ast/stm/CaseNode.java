package com.olc1.ast.stm;

import com.olc1.ast.ASTNODE;

public class CaseNode {
    private final ASTNODE value;
    private final ASTNODE body;

    public CaseNode(ASTNODE value, ASTNODE body) {
        this.value = value;
        this.body = body;
    }

    public ASTNODE getValue() {
        return value;
    }

    public ASTNODE getBody() {
        return body;
    }
}