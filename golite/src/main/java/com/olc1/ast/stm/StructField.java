package com.olc1.ast.stm;

public class StructField {
    public final String name;
    public final String type;
    public final int line;
    public final int column;

    public StructField(String name, String type, int line, int column) {
        this.name = name;
        this.type = type;
        this.line = line;
        this.column = column;
    }
}