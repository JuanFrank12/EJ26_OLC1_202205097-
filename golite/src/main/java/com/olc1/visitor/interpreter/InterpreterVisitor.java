package com.olc1.visitor.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.olc1.ast.ASTNODE;
import com.olc1.ast.exp.*;
import com.olc1.ast.stm.*;
import com.olc1.reports.GoliteError;
import com.olc1.visitor.Visitor;
import com.olc1.visitor.interpreter.value.*;
import com.olc1.visitor.interpreter.transfer.BreakException;
import com.olc1.visitor.interpreter.transfer.ContinueException;
import com.olc1.visitor.interpreter.value.RuneValue;


public class InterpreterVisitor implements Visitor<ValueWrapper> {
    public String output = "";
    private final ValueWrapper defaultVoid = new VoidValue(-1, -1);
    private Enviroment enviroment = new Enviroment();
    private int loopDepth = 0;
    public final List<GoliteError> errors = new ArrayList<>();

    private void executeInNewScope(ASTNODE body) {
    Enviroment previous = this.enviroment;

    try {
        this.enviroment = new Enviroment(previous);
        Visit(body);
    } finally {
        this.enviroment = previous;
    }
}

    public ValueWrapper Visit(ASTNODE node) {
        return node.accept(this);
    }

    @Override
    public ValueWrapper visit(Integers.Context ctx) {
        return new IntValue(ctx.value, ctx.line, ctx.column);
    }

    @Override
    public ValueWrapper visit(Decimal.Context ctx) {
        return new DecimalValue(ctx.value, ctx.line, ctx.column);
    }

    @Override
public ValueWrapper visit(Add.Context ctx) {
    ValueWrapper left = Visit(ctx.left);
    ValueWrapper right = Visit(ctx.right);

    if (hasNil(left, right, "+")) {
        return defaultVoid;
    }

    // int + int = int
    if (left instanceof IntValue l && right instanceof IntValue r) {
        return new IntValue(l.value() + r.value(), l.line(), l.column());
    }

    // int + float64 = float64
    if (left instanceof IntValue l && right instanceof DecimalValue r) {
        return new DecimalValue(l.value() + r.value(), l.line(), l.column());
    }

    // float64 + int = float64
    if (left instanceof DecimalValue l && right instanceof IntValue r) {
        return new DecimalValue(l.value() + r.value(), l.line(), l.column());
    }

    // float64 + float64 = float64
    if (left instanceof DecimalValue l && right instanceof DecimalValue r) {
        return new DecimalValue(l.value() + r.value(), l.line(), l.column());
    }

    // string + string = string
    if (left instanceof StringValue l && right instanceof StringValue r) {
        String result = l.toString() + r.toString();
        return new StringValue("\"" + result + "\"", l.line(), l.column());
    }

    this.errors.add(
        new GoliteError(
            "Semantico",
            "Operacion invalida: " + left.getTypeName() + " + " + right.getTypeName(),
            left.line(),
            left.column()
        )
    );

    return defaultVoid;
}

    @Override
    public ValueWrapper visit(Sub.Context ctx) {
        ValueWrapper left  = Visit(ctx.left);
        ValueWrapper right = Visit(ctx.right);
        if (hasNil(left, right, "-")) return defaultVoid;
        return switch (left) {
            case IntValue     l when right instanceof IntValue     r -> new IntValue((int)(l.value() - r.value()), l.line(), l.column());
            case IntValue     l when right instanceof DecimalValue r -> new DecimalValue(l.value() - r.value(), l.line(), l.column());
            case DecimalValue l when right instanceof IntValue     r -> new DecimalValue(l.value() - r.value(), l.line(), l.column());
            case DecimalValue l when right instanceof DecimalValue r -> new DecimalValue(l.value() - r.value(), l.line(), l.column());
            default -> throw new RuntimeException("Operacion invalida: " + left.getTypeName() + " - " + right.getTypeName());
        };
    }

    @Override
    public ValueWrapper visit(Mul.Context ctx) {
        ValueWrapper left  = Visit(ctx.left);
        ValueWrapper right = Visit(ctx.right);
        if (hasNil(left, right, "*")) return defaultVoid;
        return switch (left) {
            case IntValue     l when right instanceof IntValue     r -> new IntValue(l.value() * r.value(), l.line(), l.column());
            case IntValue     l when right instanceof DecimalValue r -> new DecimalValue(l.value() * r.value(), l.line(), l.column());
            case DecimalValue l when right instanceof IntValue     r -> new DecimalValue(l.value() * r.value(), l.line(), l.column());
            case DecimalValue l when right instanceof DecimalValue r -> new DecimalValue(l.value() * r.value(), l.line(), l.column());
            default -> throw new RuntimeException("Operacion invalida: " + left.getTypeName() + " * " + right.getTypeName());
        };
    }

    @Override
public ValueWrapper visit(Div.Context ctx) {
    ValueWrapper left = Visit(ctx.left);
    ValueWrapper right = Visit(ctx.right);

    if (hasNil(left, right, "/")) {
        return defaultVoid;
    }

    // int / int = int
    if (left instanceof IntValue l && right instanceof IntValue r) {
        if (r.value() == 0) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "No se puede dividir entre cero",
                    r.line(),
                    r.column()
                )
            );
            return defaultVoid;
        }

        return new IntValue(l.value() / r.value(), l.line(), l.column());
    }

    // int / float64 = float64
    if (left instanceof IntValue l && right instanceof DecimalValue r) {
        if (r.value() == 0.0) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "No se puede dividir entre cero",
                    r.line(),
                    r.column()
                )
            );
            return defaultVoid;
        }

        return new DecimalValue(l.value() / r.value(), l.line(), l.column());
    }

    // float64 / int = float64
    if (left instanceof DecimalValue l && right instanceof IntValue r) {
        if (r.value() == 0) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "No se puede dividir entre cero",
                    r.line(),
                    r.column()
                )
            );
            return defaultVoid;
        }

        return new DecimalValue(l.value() / r.value(), l.line(), l.column());
    }

    // float64 / float64 = float64
    if (left instanceof DecimalValue l && right instanceof DecimalValue r) {
        if (r.value() == 0.0) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "No se puede dividir entre cero",
                    r.line(),
                    r.column()
                )
            );
            return defaultVoid;
        }

        return new DecimalValue(l.value() / r.value(), l.line(), l.column());
    }

    this.errors.add(
        new GoliteError(
            "Semantico",
            "Operacion invalida: " + left.getTypeName() + " / " + right.getTypeName(),
            left.line(),
            left.column()
        )
    );

    return defaultVoid;
}

    @Override
    public ValueWrapper visit(Negate.Context ctx) {
        ValueWrapper operand = Visit(ctx.expression);
        if (operand instanceof NilValue) {
    this.errors.add(
        new GoliteError(
            "Semantico",
            "No se puede usar nil en negacion unaria",
            operand.line(),
            operand.column()
        )
    );
    return defaultVoid;
}
        return switch (operand) {
            case IntValue     v -> new IntValue(-v.value(), v.line(), v.column());
            case DecimalValue v -> new DecimalValue(-v.value(), v.line(), v.column());
            default -> throw new RuntimeException("Operacion invalida: -" + operand.getTypeName());
        };
    }

    @Override
public ValueWrapper visit(Imprimir.Context ctx) {
    StringBuilder line = new StringBuilder();

    for (int i = 0; i < ctx.expressions.size(); i++) {
        ValueWrapper value = Visit(ctx.expressions.get(i));

        if (i > 0) {
            line.append(" ");
        }

        line.append(value.toString());
    }

    output += line.toString() + "\n";
    return defaultVoid;
}

    @Override
    public ValueWrapper visit(Statments.Context ctx) {
        for (ASTNODE statment : ctx.statements) {
            Visit(statment);
        }

        return defaultVoid;
    }

    @Override
public ValueWrapper visit(Block.Context ctx) {
    Enviroment parentEnv = this.enviroment;

    try {
        this.enviroment = new Enviroment(parentEnv);
        Visit(ctx.body);
    } finally {
        this.enviroment = parentEnv;
    }

    return defaultVoid;
}

    @Override
    public ValueWrapper visit(Paren.Context ctx) {
        return Visit(ctx.expression);
    }

    @Override
    public ValueWrapper visit(BoolLiteral.Context ctx) {
        return new BoolValue(ctx.value, ctx.line, ctx.column);
    }

    @Override
    public ValueWrapper visit(StringLiteral.Context ctx) {
        return new StringValue(ctx.value, ctx.line, ctx.column);
    }

    @Override
    public ValueWrapper visit(VarRef.Context ctx) {
        try{
            return enviroment.get(ctx.name);
        } catch (RuntimeException e){
            this.errors.add(
                new GoliteError("Semantico", "Variable '" +ctx.name +" no declarado",
                    ctx.line,
                    ctx.column));
            return defaultVoid;
        }
    }


    @Override
    public ValueWrapper visit(Assign.Context ctx) {
        ValueWrapper val = Visit(ctx.value);

        try{
            enviroment.declare(ctx.name, val);
        } catch (RuntimeException e) {
            this.errors.add(
                new GoliteError(
                    "Semantico", 
                    "Variable '"+ ctx.name+ "' ya declarada en este ambito",
                    ctx.line,
                    ctx.column));
        }
        return defaultVoid;
    }

    @Override
    public ValueWrapper visit(IfNode.Context ctx) {
        Enviroment parentEnv = this.enviroment;
        ValueWrapper cond = Visit(ctx.condition);

        if (cond instanceof BoolValue b && b.value()) {
            this.enviroment = new Enviroment(parentEnv);
            Visit(ctx.body);
            this.enviroment = parentEnv;
            return defaultVoid;
        }

        ElifNodes elifList = ctx.elifList;
        if(elifList != null){
            Visit(elifList);

            for (ElifNode elif: elifList.ctx.elifNodesList){
                Visit(elif);
                ValueWrapper elifCondition = Visit(elif.ctx.condition);

                if (elifCondition instanceof BoolValue eb && eb.value()){
                    this.enviroment = new Enviroment(parentEnv);
                    Visit(elif.ctx.body);
                    this.enviroment = parentEnv;
                    return defaultVoid;
                }
            }
        }
        return defaultVoid;
    } 
    
    


    @Override
    public ValueWrapper visit(ElifNode.Context ctx){
        return defaultVoid;
    }

    @Override
    public ValueWrapper visit(ElifNodes.Context ctx){
        return defaultVoid;
    }

   @Override
public ValueWrapper visit(WhileFor.Context ctx) {
    int contadorSeguro = 0;

    Enviroment parentEnv = this.enviroment;
    boolean hasForInit = ctx.init != null;

    if (hasForInit) {
        this.enviroment = new Enviroment(parentEnv);
        Visit(ctx.init);
    }

    loopDepth++;

    try {
        while (true) {
            ValueWrapper condition = Visit(ctx.condition);

            if (!(condition instanceof BoolValue b)) {
                this.errors.add(
                    new GoliteError(
                        "Semantico",
                        "La condicion del for debe ser booleana",
                        -1,
                        -1
                    )
                );
                return defaultVoid;
            }

            if (!b.value()) {
                break;
            }

            if (contadorSeguro > 10000) {
                this.errors.add(
                    new GoliteError(
                        "Semantico",
                        "Posible ciclo infinito en for",
                        -1,
                        -1
                    )
                );
                break;
            }

            try {
                // IMPORTANTE:
                // Cada vuelta del for ejecuta su cuerpo en un nuevo ámbito.
                executeInNewScope(ctx.body);

            } catch (ContinueException e) {
                if (ctx.update != null) {
                    Visit(ctx.update);
                }

                contadorSeguro++;
                continue;

            } catch (BreakException e) {
                break;
            }

            if (ctx.update != null) {
                Visit(ctx.update);
            }

            contadorSeguro++;
        }
    } finally {
        loopDepth--;

        if (hasForInit) {
            this.enviroment = parentEnv;
        }
    }

    return defaultVoid;
}


@Override
public ValueWrapper visit(VarDecl.Context ctx) {
    ValueWrapper value;

    if (ctx.value == null) {
        value = defaultValueForType(ctx.type, ctx.line, ctx.column);
    } else {
        ValueWrapper evaluated = Visit(ctx.value);
        value = castToDeclaredType(ctx.type, evaluated, ctx.line, ctx.column);

        if (value instanceof VoidValue) {
            return defaultVoid;
        }
    }

    try {
        enviroment.declare(ctx.name, value);
    } catch (RuntimeException e) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "Variable '" + ctx.name + "' ya declarada en este ambito",
                ctx.line,
                ctx.column
            )
        );
    }

    return defaultVoid;
}

@Override
public ValueWrapper visit(RuneLiteral.Context ctx) {
    return new RuneValue(parseRune(ctx.value), ctx.line, ctx.column);
}

private ValueWrapper defaultValueForType(String type, int line, int column) {
    return switch (type) {
        case "int" -> new IntValue(0, line, column);
        case "float64" -> new DecimalValue(0.0, line, column);
        case "string" -> new StringValue("\"\"", line, column);
        case "bool" -> new BoolValue(false, line, column);
        case "rune" -> new RuneValue((char) 0, line, column);

        default -> {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "Tipo no reconocido: " + type,
                    line,
                    column
                )
            );

            yield defaultVoid;
        }
    };
}

private ValueWrapper castToDeclaredType(String type, ValueWrapper value, int line, int column) {
    if (value instanceof NilValue) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "No se puede asignar nil a una variable de tipo primitivo '" + type + "'",
                line,
                column
            )
        );

        return defaultVoid;
    }

    return switch (type) {
        case "int" -> {
            if (value instanceof IntValue) {
                yield value;
            }

            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "No se puede asignar un valor de tipo '" + value.getTypeName() + "' a una variable de tipo 'int'",
                    line,
                    column
                )
            );

            yield defaultVoid;
        }

        case "float64" -> {
            if (value instanceof DecimalValue) {
                yield value;
            }

            if (value instanceof IntValue v) {
                yield new DecimalValue(v.value(), v.line(), v.column());
            }

            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "No se puede asignar un valor de tipo '" + value.getTypeName() + "' a una variable de tipo 'float64'",
                    line,
                    column
                )
            );

            yield defaultVoid;
        }

        case "string" -> {
            if (value instanceof StringValue) {
                yield value;
            }

            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "No se puede asignar un valor de tipo '" + value.getTypeName() + "' a una variable de tipo 'string'",
                    line,
                    column
                )
            );

            yield defaultVoid;
        }

        case "bool" -> {
            if (value instanceof BoolValue) {
                yield value;
            }

            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "No se puede asignar un valor de tipo '" + value.getTypeName() + "' a una variable de tipo 'bool'",
                    line,
                    column
                )
            );

            yield defaultVoid;
        }

        case "rune" -> {
            if (value instanceof RuneValue) {
                yield value;
            }

            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "No se puede asignar un valor de tipo '" + value.getTypeName() + "' a una variable de tipo 'rune'",
                    line,
                    column
                )
            );

            yield defaultVoid;
        }

        default -> {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "Tipo no reconocido: " + type,
                    line,
                    column
                )
            );

            yield defaultVoid;
        }
    };
}

private char parseRune(String raw) {
    String content = raw.substring(1, raw.length() - 1);

    if (content.startsWith("\\")) {
        return switch (content.charAt(1)) {
            case 'n' -> '\n';
            case 'r' -> '\r';
            case 't' -> '\t';
            case '\'' -> '\'';
            case '\\' -> '\\';
            default -> content.charAt(1);
        };
    }

    return content.charAt(0);
}

@Override
public ValueWrapper visit(EmbeddedFunction.Context ctx) {

    if (ctx.functionName.equals("TypeOfString")) {
        ValueWrapper value = Visit(ctx.expression);

        if (value == null) {
            return new StringValue("\"void\"", ctx.line, ctx.column);
        }

        return new StringValue("\"" + value.getTypeName() + "\"", ctx.line, ctx.column);
    }

    if (ctx.functionName.equals("Atoi")) {
        ValueWrapper value = Visit(ctx.expression);

        if (value instanceof StringValue s) {
            try {
                int result = Integer.parseInt(s.toString());
                return new IntValue(result, ctx.line, ctx.column);
            } catch (NumberFormatException e) {
                this.errors.add(
                    new GoliteError(
                        "Semantico",
                        "strconv.Atoi no puede convertir '" + s.toString() + "' a int",
                        ctx.line,
                        ctx.column
                    )
                );
                return defaultVoid;
            }
        }

        this.errors.add(
            new GoliteError(
                "Semantico",
                "strconv.Atoi espera un valor de tipo string",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    if (ctx.functionName.equals("ParseFloat")) {
        ValueWrapper value = Visit(ctx.expression);

        if (value instanceof StringValue s) {
            try {
                double result = Double.parseDouble(s.toString());
                return new DecimalValue(result, ctx.line, ctx.column);
            } catch (NumberFormatException e) {
                this.errors.add(
                    new GoliteError(
                        "Semantico",
                        "strconv.ParseFloat no puede convertir '" + s.toString() + "' a float64",
                        ctx.line,
                        ctx.column
                    )
                );
                return defaultVoid;
            }
        }

        this.errors.add(
            new GoliteError(
                "Semantico",
                "strconv.ParseFloat espera un valor de tipo string",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    this.errors.add(
        new GoliteError(
            "Semantico",
            "Funcion embebida no reconocida: " + ctx.functionName,
            ctx.line,
            ctx.column
        )
    );

    return defaultVoid;
}


@Override
public ValueWrapper visit(BreakStm.Context ctx) {
    if (loopDepth <= 0) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "La sentencia break solo puede usarse dentro de un ciclo",
                ctx.line,
                ctx.column
            )
        );
        return defaultVoid;
    }

    throw new BreakException();
}

@Override
public ValueWrapper visit(ContinueStm.Context ctx) {
    if (loopDepth <= 0) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "La sentencia continue solo puede usarse dentro de un ciclo",
                ctx.line,
                ctx.column
            )
        );
        return defaultVoid;
    }

    throw new ContinueException();
}

@Override
public ValueWrapper visit(LowerTag.Context ctx) {
    ValueWrapper left = Visit(ctx.left);
    ValueWrapper right = Visit(ctx.right);
    if (hasNil(left, right, "<")) return defaultVoid;

    return switch (left) {
        case IntValue l when right instanceof IntValue r ->
            new BoolValue(l.value() < r.value(), l.line(), l.column());

        case IntValue l when right instanceof DecimalValue r ->
            new BoolValue(l.value() < r.value(), l.line(), l.column());

        case DecimalValue l when right instanceof IntValue r ->
            new BoolValue(l.value() < r.value(), l.line(), l.column());

        case DecimalValue l when right instanceof DecimalValue r ->
            new BoolValue(l.value() < r.value(), l.line(), l.column());

        default -> throw new RuntimeException(
            "Operacion invalida: " + left.getTypeName() + " < " + right.getTypeName()
        );
    };
}

@Override
public ValueWrapper visit(AllLocate.Context ctx) {
    ValueWrapper val = Visit(ctx.expression);

    try {
        enviroment.set(ctx.id, val);
    } catch (RuntimeException e) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                e.getMessage(),
                ctx.line,
                ctx.column
            )
        );
    }

    return defaultVoid;
}

@Override
public ValueWrapper visit(Mod.Context ctx) {
    ValueWrapper left = Visit(ctx.left);
    ValueWrapper right = Visit(ctx.right);

    if (hasNil(left, right, "%")) return defaultVoid;

    if (left instanceof IntValue l && right instanceof IntValue r) {
        if (r.value() == 0) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "No se puede hacer modulo entre cero",
                    r.line(),
                    r.column()
                )
            );
            return defaultVoid;
        }

        return new IntValue(l.value() % r.value(), l.line(), l.column());
    }

    this.errors.add(
        new GoliteError(
            "Semantico",
            "Operacion invalida: " + left.getTypeName() + " % " + right.getTypeName(),
            left.line(),
            left.column()
        )
    );

    return defaultVoid;
}

@Override
public ValueWrapper visit(Compare.Context ctx) {
    ValueWrapper left = Visit(ctx.left);
    ValueWrapper right = Visit(ctx.right);

    String op = ctx.operator;

    // Igualdad y desigualdad
    if (op.equals("==") || op.equals("!=")) {
        boolean result;

        if (left instanceof IntValue l && right instanceof IntValue r) {
            result = l.value() == r.value();

        } else if (left instanceof IntValue l && right instanceof DecimalValue r) {
            result = l.value() == r.value();

        } else if (left instanceof DecimalValue l && right instanceof IntValue r) {
            result = l.value() == r.value();

        } else if (left instanceof DecimalValue l && right instanceof DecimalValue r) {
            result = l.value() == r.value();

        } else if (left instanceof BoolValue l && right instanceof BoolValue r) {
            result = l.value() == r.value();

        } else if (left instanceof StringValue l && right instanceof StringValue r) {
            result = l.toString().equals(r.toString());

        } else if (left instanceof RuneValue l && right instanceof RuneValue r) {
            result = l.value() == r.value();

        } else if (left instanceof RuneValue l && right instanceof IntValue r) {
            result = ((int) l.value()) == r.value();

        } else if (left instanceof IntValue l && right instanceof RuneValue r) {
            result = l.value() == ((int) r.value());

        } else if (left instanceof NilValue && right instanceof NilValue) {
            result = true;

        } else if (left instanceof NilValue || right instanceof NilValue) {
            result = false;

        } else {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "Operacion invalida: " + left.getTypeName() + " " + op + " " + right.getTypeName(),
                    left.line(),
                    left.column()
                )
            );
            return defaultVoid;
        }

        if (op.equals("!=")) {
            result = !result;
        }

        return new BoolValue(result, left.line(), left.column());
    }

    // Relacionales > >= <=
    if (hasNil(left, right, op)) {
        return defaultVoid;
    }

    if (left instanceof IntValue l && right instanceof IntValue r) {
        return new BoolValue(compareNumbers(l.value(), r.value(), op), l.line(), l.column());
    }

    if (left instanceof IntValue l && right instanceof DecimalValue r) {
        return new BoolValue(compareNumbers(l.value(), r.value(), op), l.line(), l.column());
    }

    if (left instanceof DecimalValue l && right instanceof IntValue r) {
        return new BoolValue(compareNumbers(l.value(), r.value(), op), l.line(), l.column());
    }

    if (left instanceof DecimalValue l && right instanceof DecimalValue r) {
        return new BoolValue(compareNumbers(l.value(), r.value(), op), l.line(), l.column());
    }

    if (left instanceof RuneValue l && right instanceof RuneValue r) {
        return new BoolValue(compareNumbers((int) l.value(), (int) r.value(), op), l.line(), l.column());
    }

    if (left instanceof RuneValue l && right instanceof IntValue r) {
        return new BoolValue(compareNumbers((int) l.value(), r.value(), op), l.line(), l.column());
    }

    if (left instanceof IntValue l && right instanceof RuneValue r) {
        return new BoolValue(compareNumbers(l.value(), (int) r.value(), op), l.line(), l.column());
    }

    this.errors.add(
        new GoliteError(
            "Semantico",
            "Operacion invalida: " + left.getTypeName() + " " + op + " " + right.getTypeName(),
            left.line(),
            left.column()
        )
    );

    return defaultVoid;
}


private boolean compareNumbers(double left, double right, String op) {
    return switch (op) {
        case ">" -> left > right;
        case ">=" -> left >= right;
        case "<=" -> left <= right;
        default -> false;
    };
}

@Override
public ValueWrapper visit(Logical.Context ctx) {
    String op = ctx.operator;

    // NOT: !expresion
    if (op.equals("!")) {
        ValueWrapper value = Visit(ctx.left);

        if (value instanceof BoolValue b) {
            return new BoolValue(!b.value(), b.line(), b.column());
        }

        this.errors.add(
            new GoliteError(
                "Semantico",
                "El operador '!' necesita un valor booleano",
                value.line(),
                value.column()
            )
        );

        return defaultVoid;
    }

    // AND y OR
    ValueWrapper left = Visit(ctx.left);

    if (!(left instanceof BoolValue l)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "El operador '" + op + "' necesita valores booleanos",
                left.line(),
                left.column()
            )
        );
        return defaultVoid;
    }

    // Cortocircuito &&
    if (op.equals("&&")) {
        if (!l.value()) {
            return new BoolValue(false, l.line(), l.column());
        }

        ValueWrapper right = Visit(ctx.right);

        if (!(right instanceof BoolValue r)) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "El operador '&&' necesita valores booleanos",
                    right.line(),
                    right.column()
                )
            );
            return defaultVoid;
        }

        return new BoolValue(l.value() && r.value(), l.line(), l.column());
    }

    // Cortocircuito ||
    if (op.equals("||")) {
        if (l.value()) {
            return new BoolValue(true, l.line(), l.column());
        }

        ValueWrapper right = Visit(ctx.right);

        if (!(right instanceof BoolValue r)) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "El operador '||' necesita valores booleanos",
                    right.line(),
                    right.column()
                )
            );
            return defaultVoid;
        }

        return new BoolValue(l.value() || r.value(), l.line(), l.column());
    }

    return defaultVoid;
}

@Override
public ValueWrapper visit(NilLiteral.Context ctx) {
    return new NilValue(ctx.line, ctx.column);
}

private boolean hasNil(ValueWrapper left, ValueWrapper right, String op) {
    if (left instanceof NilValue || right instanceof NilValue) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "No se puede usar nil en la operacion '" + op + "'",
                left.line(),
                left.column()
            )
        );
        return true;
    }

    return false;
}

}

    
