package com.olc1.reports;

public class TokenReport {
    private final String lexeme;
    private final String tokenType;
    private final int line;
    private final int column;

    public TokenReport(String lexeme, String tokenType, int line, int column) {
        this.lexeme = lexeme;
        this.tokenType = tokenType;
        this.line = line;
        this.column = column;
    }

    public String getLexeme() {
        return lexeme;
    }

    public String getTokenType() {
        return tokenType;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}