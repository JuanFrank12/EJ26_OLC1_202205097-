package com.olc1.reports;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.olc1.ast.ASTNODE;

public class ASTDotGenerator {
    private final StringBuilder dot = new StringBuilder();
    private int counter = 0;
    private final Map<Object, Integer> visited = new IdentityHashMap<>();

    public String generate(ASTNODE ast) {
        dot.setLength(0);
        counter = 0;
        visited.clear();

        dot.append("digraph AST {\n");
        dot.append("  graph [rankdir=TB];\n");
        dot.append("  node [shape=plain, fontname=\"Arial\"];\n");
        dot.append("  edge [arrowsize=0.7];\n\n");

        int root = addNode("INICIO");

        if (ast == null) {
            int empty = addNode("No hay AST generado");
            addEdge(root, empty);
        } else {
            int astNode = buildNode(ast, "AST");
            addEdge(root, astNode);
        }

        dot.append("}\n");
        return dot.toString();
    }

    private int buildNode(Object obj, String fieldName) {
        if (obj == null) {
            return addNode("null");
        }

        if (isPrimitiveLike(obj)) {
            return addNode(formatPrimitive(fieldName, obj, ""));
        }

        if (obj instanceof List<?> list) {
            int listNode = addNode(formatFieldName(fieldName));

            for (Object item : list) {
                int child = buildNode(item, getSimpleName(item));
                addEdge(listNode, child);
            }

            return listNode;
        }

        if (visited.containsKey(obj)) {
            return visited.get(obj);
        }

        int current = addNode(labelForObject(obj));
        visited.put(obj, current);

        for (Field field : obj.getClass().getDeclaredFields()) {
            try {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                if (field.isSynthetic()) {
                    continue;
                }

                String name = field.getName();

                if (name.equals("ctx")) {
                    continue;
                }

                if (name.equals("line") || name.equals("column")) {
                    continue;
                }

                field.setAccessible(true);
                Object value = field.get(obj);

                if (value == null) {
                    continue;
                }

                if (isPrimitiveLike(value)) {
                    int leaf = addNode(formatPrimitive(name, value, obj.getClass().getSimpleName()));
                    addEdge(current, leaf);
                } else if (value instanceof List<?> list) {
                    int listNode = addNode(formatFieldName(name));
                    addEdge(current, listNode);

                    for (Object item : list) {
                        int child = buildNode(item, getSimpleName(item));
                        addEdge(listNode, child);
                    }
                } else {
                    int child = buildNode(value, name);
                    addEdge(current, child);
                }

            } catch (Exception e) {
                int errorNode = addNode(field.getName() + ": <no accesible>");
                addEdge(current, errorNode);
            }
        }

        return current;
    }

    private int addNode(String label) {
        int id = counter++;
        dot.append("  n").append(id)
           .append(" [label=\"")
           .append(escape(label))
           .append("\"];\n");
        return id;
    }

    private void addEdge(int from, int to) {
        dot.append("  n").append(from)
           .append(" -> n").append(to)
           .append(";\n");
    }

    private String labelForObject(Object obj) {
        String name = obj.getClass().getSimpleName();

        return switch (name) {
            case "Statments" -> "INSTRUCCIONES";
            case "Block" -> "BLOQUE";
            case "MainFunction" -> "FUNC MAIN";
            case "FunctionDecl" -> "FUNCION";
            case "MethodDecl" -> "METODO";
            case "ReturnStm" -> "RETURN";

            case "Assign" -> "ASIGNACION";
            case "VarDecl" -> "DECLARACION";
            case "AllLocate" -> "REASIGNACION";

            case "IfNode" -> "IF";
            case "ElifNodes" -> "ELSE IF / ELSE";
            case "ElifNode" -> "RAMA";
            case "WhileFor" -> "FOR";
            case "ForRange" -> "FOR RANGE";
            case "SwitchNode" -> "SWITCH";
            case "CaseNode" -> "CASE";
            case "DefaultNode" -> "DEFAULT";
            case "BreakStm" -> "BREAK";
            case "ContinueStm" -> "CONTINUE";

            case "Imprimir" -> "IMPRIMIR";

            case "Add" -> "+";
            case "Sub" -> "-";
            case "Mul" -> "*";
            case "Div" -> "/";
            case "Mod" -> "%";
            case "Negate" -> "NEGATIVO";
            case "Compare" -> "COMPARACION";
            case "Logical" -> "LOGICO";

            case "Paren" -> "EXPRESION";
            case "Integers" -> "EXPRESION";
            case "Decimal" -> "EXPRESION";
            case "StringLiteral" -> "EXPRESION";
            case "BoolLiteral" -> "EXPRESION";
            case "RuneLiteral" -> "EXPRESION";
            case "NilLiteral" -> "NIL";
            case "VarRef" -> "EXPRESION";

            case "SliceLiteral" -> "SLICE";
            case "SliceAccess" -> "ACCESO SLICE";
            case "SliceAssign" -> "ASIGNACION SLICE";
            case "AppendFunction" -> "APPEND";
            case "SlicesIndexFunction" -> "SLICES.INDEX";
            case "StringsJoinFunction" -> "STRINGS.JOIN";

            case "StructTypeDecl" -> "STRUCT";
            case "StructLiteral" -> "STRUCT LITERAL";
            case "StructAccess" -> "ACCESO STRUCT";
            case "StructAssign" -> "ASIGNACION STRUCT";

            case "FunctionCall" -> "LLAMADA FUNCION";
            case "MethodCall" -> "LLAMADA METODO";
            case "EmbeddedFunction" -> "FUNCION EMBEBIDA";

            default -> name.toUpperCase();
        };
    }

    private String formatPrimitive(String fieldName, Object value, String ownerClass) {
        if (ownerClass.equals("Integers") && fieldName.equals("value")) {
            return "<integer, " + value + ">";
        }

        if (ownerClass.equals("Decimal") && fieldName.equals("value")) {
            return "<decimal, " + value + ">";
        }

        if (ownerClass.equals("StringLiteral") && fieldName.equals("value")) {
            return "<string, " + value + ">";
        }

        if (ownerClass.equals("BoolLiteral") && fieldName.equals("value")) {
            return "<bool, " + value + ">";
        }

        if (ownerClass.equals("RuneLiteral") && fieldName.equals("value")) {
            return "<rune, " + value + ">";
        }

        if (ownerClass.equals("VarRef") && fieldName.equals("name")) {
            return "<id, " + value + ">";
        }

        if (fieldName.equals("name") || fieldName.equals("id")) {
            return "<id, " + value + ">";
        }

        if (fieldName.equals("operator")) {
            return "<op, " + value + ">";
        }

        if (fieldName.equals("type") || fieldName.equals("returnType")) {
            return "<tipo, " + value + ">";
        }

        return "<" + fieldName + ", " + value + ">";
    }

    private String formatFieldName(String fieldName) {
        return switch (fieldName) {
            case "statements" -> "INSTRUCCIONES";
            case "body" -> "CUERPO";
            case "condition" -> "CONDICION";
            case "left" -> "EXPRESION";
            case "right" -> "EXPRESION";
            case "expression" -> "EXPRESION";
            case "expressions" -> "ARGS";
            case "arguments" -> "ARGS";
            case "params" -> "PARAMETROS";
            case "fields" -> "ATRIBUTOS";
            case "cases" -> "CASES";
            case "defaultBody" -> "DEFAULT";
            case "value" -> "VALOR";
            default -> fieldName.toUpperCase();
        };
    }

    private boolean isPrimitiveLike(Object obj) {
        return obj instanceof String
                || obj instanceof Number
                || obj instanceof Boolean
                || obj instanceof Character
                || obj.getClass().isPrimitive();
    }

    private String getSimpleName(Object obj) {
        if (obj == null) {
            return "null";
        }

        return obj.getClass().getSimpleName();
    }

    private String escape(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}