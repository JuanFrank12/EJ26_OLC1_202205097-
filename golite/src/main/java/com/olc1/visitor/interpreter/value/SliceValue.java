package com.olc1.visitor.interpreter.value;

import java.util.List;

public record SliceValue(String elementType, List<ValueWrapper> values, int line, int column) implements ValueWrapper {

    @Override
    public String getTypeName() {
        return "[]" + elementType;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}