package com.olc1.reports;

public class SymbolReport {
    private final String id;
    private final String symbolType;
    private final String dataType;
    private final String scope;
    private final int line;
    private final int column;

    public SymbolReport(String id, String symbolType, String dataType, String scope, int line, int column) {
        this.id = id;
        this.symbolType = symbolType;
        this.dataType = dataType;
        this.scope = scope;
        this.line = line;
        this.column = column;
    }

    public String getId() {
        return id;
    }

    public String getSymbolType() {
        return symbolType;
    }

    public String getDataType() {
        return dataType;
    }

    public String getScope() {
        return scope;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}