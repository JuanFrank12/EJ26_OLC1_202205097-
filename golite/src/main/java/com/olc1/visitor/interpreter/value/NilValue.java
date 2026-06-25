package com.olc1.visitor.interpreter.value;

public record NilValue(int line, int column) implements ValueWrapper {
    @Override
    public String getTypeName() {
        return "nil";
    }

    @Override
    public String toString() {
        return "<nil>";
    }
}