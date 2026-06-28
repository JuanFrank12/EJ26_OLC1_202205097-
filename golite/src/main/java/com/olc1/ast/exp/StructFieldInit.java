package com.olc1.ast.exp;

import com.olc1.ast.ASTNODE;

public class StructFieldInit {
    public final String name;
    public final ASTNODE value;
    public final int line;
    public final int column;

    public StructFieldInit(String name, ASTNODE value, int line, int column) {
        this.name = name;
        this.value = value;
        this.line = line;
        this.column = column;
    }
}