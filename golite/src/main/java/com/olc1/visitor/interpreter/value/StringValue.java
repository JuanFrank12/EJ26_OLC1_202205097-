package com.olc1.visitor.interpreter.value;

public record StringValue(String value, int line, int column) implements ValueWrapper {

    @Override
    public String getTypeName() {
        return "string";
    }

    @Override
    public String toString() {
        String raw = value.substring(1, value.length() - 1);

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < raw.length(); i++) {
            char current = raw.charAt(i);

            if (current == '\\' && i + 1 < raw.length()) {
                char next = raw.charAt(i + 1);

                switch (next) {
                    case 'n' -> result.append('\n');
                    case 'r' -> result.append('\r');
                    case 't' -> result.append('\t');
                    case '"' -> result.append('"');
                    case '\\' -> result.append('\\');
                    default -> result.append(next);
                }

                i++;
            } else {
                result.append(current);
            }
        }

        return result.toString();
    }
}