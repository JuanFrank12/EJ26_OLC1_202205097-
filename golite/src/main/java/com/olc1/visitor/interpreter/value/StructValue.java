package com.olc1.visitor.interpreter.value;

import java.util.LinkedHashMap;
import java.util.Map;

public record StructValue(String structName, Map<String, ValueWrapper> fields, int line, int column) implements ValueWrapper {

    @Override
    public String getTypeName() {
        return structName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(structName).append("{");

        int i = 0;

        for (Map.Entry<String, ValueWrapper> entry : fields.entrySet()) {
            if (i > 0) {
                sb.append(", ");
            }

            sb.append(entry.getKey()).append(": ").append(entry.getValue().toString());
            i++;
        }

        sb.append("}");
        return sb.toString();
    }

    public StructValue copy() {
        return new StructValue(
            structName,
            new LinkedHashMap<>(fields),
            line,
            column
        );
    }
}