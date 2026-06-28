package com.olc1.visitor.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;

import com.olc1.ast.ASTNODE;
import com.olc1.ast.exp.*;
import com.olc1.ast.stm.*;
import com.olc1.reports.GoliteError;
import com.olc1.reports.SymbolReport;
import com.olc1.visitor.Visitor;
import com.olc1.visitor.interpreter.value.*;
import com.olc1.visitor.interpreter.transfer.BreakException;
import com.olc1.visitor.interpreter.transfer.ContinueException;
import com.olc1.visitor.interpreter.transfer.ReturnException;
import com.olc1.visitor.interpreter.transfer.BreakException;
import com.olc1.visitor.interpreter.transfer.ContinueException;



public class InterpreterVisitor implements Visitor<ValueWrapper> {
    public String output = "";
    private final ValueWrapper defaultVoid = new VoidValue(-1, -1);
    private Enviroment enviroment = new Enviroment();
    private int loopDepth = 0;
    private int switchDepth = 0;
    private final Map<String, FunctionDecl.Context> functions = new HashMap<>();
    private final Map<String, StructTypeDecl.Context> structTypes = new HashMap<>();
    private int statmentsDepth = 0;
    private final Map<String, MethodDecl.Context> methods = new HashMap<>();
    public final List<GoliteError> errors = new ArrayList<>();
    public final List<SymbolReport> symbols = new ArrayList<>();
    private final Set<String> symbolKeys = new HashSet<>();
    private String currentScope = "Global";

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
    if (node == null) {
        return defaultVoid;
    }

    try {
        return node.accept(this);

    } catch (BreakException e) {
        throw e;

    } catch (ContinueException e) {
        throw e;

    } catch (ReturnException e) {
        throw e;

    } catch (RuntimeException e) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "Error semantico no soportado capturado: " + e.getMessage(),
                -1,
                -1
            )
        );

        return defaultVoid;
    }
}

@Override
public ValueWrapper visit(ReturnStm.Context ctx) {
    ValueWrapper value = defaultVoid;

    if (ctx.expression != null) {
        value = Visit(ctx.expression);
    }

    throw new ReturnException(value);
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
public ValueWrapper visit(MethodDecl.Context ctx) {

    if (statmentsDepth > 1) {
    this.errors.add(
        new GoliteError(
            "Semantico",
            "El metodo '" + ctx.methodName + "' solo puede declararse en el ambito global",
            ctx.line,
            ctx.column
        )
    );

    return defaultVoid;
}

    String key = ctx.receiverType + "." + ctx.methodName;

    if (!structTypes.containsKey(ctx.receiverType)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "No se puede declarar metodo para el struct '" + ctx.receiverType + "' porque no existe",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    if (methods.containsKey(key)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "El metodo '" + ctx.methodName + "' ya fue declarado para el struct '" + ctx.receiverType + "'",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    Set<String> paramNames = new HashSet<>();

    for (FunctionParam param : ctx.params) {
        if (paramNames.contains(param.name)) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "El parametro '" + param.name + "' esta repetido en el metodo '" + ctx.methodName + "'",
                    param.line,
                    param.column
                )
            );

            return defaultVoid;
        }

        if (param.name.equals(ctx.receiverName)) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "El parametro '" + param.name + "' no puede tener el mismo nombre que el receiver del metodo",
                    param.line,
                    param.column
                )
            );

            return defaultVoid;
        }

        if (!isValidDeclaredType(param.type)) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "Tipo no reconocido en parametro '" + param.name + "': " + param.type,
                    param.line,
                    param.column
                )
            );

            return defaultVoid;
        }

        paramNames.add(param.name);
    }
    String methodType = ctx.returnType == null ? "void" : ctx.returnType;
String methodScope = ctx.receiverType + "." + ctx.methodName;

addSymbol(ctx.methodName, "Metodo", methodType, "Global", ctx.line, ctx.column);
addSymbol(ctx.receiverName, "Receiver", ctx.receiverType, methodScope, ctx.line, ctx.column);

for (FunctionParam param : ctx.params) {
    addSymbol(param.name, "Parametro", param.type, methodScope, param.line, param.column);
}
    methods.put(key, ctx);
    return defaultVoid;
}

@Override
public ValueWrapper visit(MethodCall.Context ctx) {
    ValueWrapper targetValue = Visit(ctx.target);

    if (!(targetValue instanceof StructValue structValue)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "Solo se pueden llamar metodos desde un struct",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    String key = structValue.structName() + "." + ctx.methodName;
    MethodDecl.Context method = methods.get(key);

    if (method == null) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "El struct '" + structValue.structName()
                + "' no tiene el metodo '" + ctx.methodName + "'",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    if (ctx.arguments.size() != method.params.size()) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "El metodo '" + ctx.methodName + "' esperaba "
                + method.params.size() + " argumentos, pero recibio "
                + ctx.arguments.size(),
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    java.util.List<ValueWrapper> argumentValues = new java.util.ArrayList<>();

    for (ASTNODE argument : ctx.arguments) {
        argumentValues.add(Visit(argument));
    }

    Enviroment previous = this.enviroment;
    String previousScope = this.currentScope;

    try {
        this.enviroment = new Enviroment(previous);
        this.currentScope = structValue.structName() + "." + ctx.methodName;

        // Receiver: func (p Persona) cambiarNombre(...)
        // IMPORTANTE: sin .copy(), para que el metodo modifique el struct real
        this.enviroment.declare(method.receiverName, structValue);

        // Parametros del metodo
        for (int i = 0; i < method.params.size(); i++) {
            FunctionParam param = method.params.get(i);
            ValueWrapper argumentValue = argumentValues.get(i);

            if (!isParameterTypeCompatible(param.type, argumentValue)) {
                this.errors.add(
                    new GoliteError(
                        "Semantico",
                        "El parametro '" + param.name + "' del metodo '" + ctx.methodName
                        + "' esperaba tipo '" + param.type
                        + "' pero recibio '" + argumentValue.getTypeName() + "'",
                        param.line,
                        param.column
                    )
                );

                return defaultVoid;
            }

            ValueWrapper valueToDeclare = convertParameterValue(param.type, argumentValue);
            this.enviroment.declare(param.name, valueToDeclare);
        }

        Visit(method.body);

        if (method.returnType != null) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "El metodo '" + ctx.methodName
                    + "' debe retornar un valor de tipo '" + method.returnType + "'",
                    ctx.line,
                    ctx.column
                )
            );
        }

        return defaultVoid;

    } catch (ReturnException e) {
        ValueWrapper returnedValue = e.getValue();

        if (method.returnType == null) {
            if (!returnedValue.getTypeName().equals("void")) {
                this.errors.add(
                    new GoliteError(
                        "Semantico",
                        "El metodo '" + ctx.methodName + "' no debe retornar valor",
                        ctx.line,
                        ctx.column
                    )
                );
            }

            return defaultVoid;
        }

        if (!isReturnTypeCompatible(method.returnType, returnedValue)) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "El metodo '" + ctx.methodName + "' debe retornar '"
                    + method.returnType + "' pero retorno '"
                    + returnedValue.getTypeName() + "'",
                    ctx.line,
                    ctx.column
                )
            );

            return defaultVoid;
        }

        return returnedValue;

    } finally {
    this.enviroment = previous;
    this.currentScope = previousScope;
}
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
    boolean isTopLevel = statmentsDepth == 0;
    statmentsDepth++;

    try {
        if (isTopLevel) {
            MainFunction mainFunction = null;

            // 1. Primero registramos structs globales
            for (ASTNODE statement : ctx.statements) {
                if (statement instanceof StructTypeDecl) {
                    Visit(statement);
                }
            }

            // 2. Luego registramos metodos, funciones y buscamos main
            for (ASTNODE statement : ctx.statements) {
                if (statement instanceof MethodDecl) {
                    Visit(statement);
                } else if (statement instanceof FunctionDecl) {
                    Visit(statement);
                } else if (statement instanceof MainFunction main) {
                    mainFunction = main;
                }
            }

            // Si ya hubo errores al registrar structs, funciones o métodos,
            // NO ejecutamos main ni codigo suelto.
            if (!this.errors.isEmpty()) {
                return defaultVoid;
            }

            // 3. Si existe main, se ejecuta solo main
            if (mainFunction != null) {
                return Visit(mainFunction);
            }

            // 4. Compatibilidad: si no hay main, ejecuta codigo suelto
            for (ASTNODE statement : ctx.statements) {
                if (!(statement instanceof StructTypeDecl)
                        && !(statement instanceof MethodDecl)
                        && !(statement instanceof FunctionDecl)
                        && !(statement instanceof MainFunction)) {
                    Visit(statement);

                    if (!this.errors.isEmpty()) {
                        return defaultVoid;
                    }
                }
            }

            return defaultVoid;
        }

        // Si NO estamos en nivel global, ejecutamos normal.
        // Las declaraciones globales inválidas se validan dentro de sus propios visitors.
        for (ASTNODE statement : ctx.statements) {
            Visit(statement);

            if (!this.errors.isEmpty()) {
                return defaultVoid;
            }
        }

        return defaultVoid;

    } finally {
        statmentsDepth--;
    }
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

if (!(val instanceof VoidValue)) {
    addSymbol(ctx.name, "Variable", val.getTypeName(), currentScope, ctx.line, ctx.column);
}
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

    if (!(cond instanceof BoolValue b)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "La condicion del if debe ser booleana",
                cond.line(),
                cond.column()
            )
        );

        return defaultVoid;
    }

    if (b.value()) {
        this.enviroment = new Enviroment(parentEnv);

        try {
            Visit(ctx.body);
        } finally {
            this.enviroment = parentEnv;
        }

        return defaultVoid;
    }

    ElifNodes elifList = ctx.elifList;

    if (elifList != null) {
        for (ElifNode elif : elifList.getElifNodesList()) {
    Visit(elif);

    ValueWrapper elifCondition = Visit(elif.ctx.condition);

    if (!(elifCondition instanceof BoolValue eb)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "La condicion del else if debe ser booleana",
                elifCondition.line(),
                elifCondition.column()
            )
        );

        return defaultVoid;
    }

    if (eb.value()) {
        this.enviroment = new Enviroment(parentEnv);

        try {
            Visit(elif.ctx.body);
        } finally {
            this.enviroment = parentEnv;
        }

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

    if (!isValidDeclaredType(ctx.type)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "Tipo no reconocido: " + ctx.type,
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

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

if (!(value instanceof VoidValue)) {
    addSymbol(ctx.name, "Variable", ctx.type, currentScope, ctx.line, ctx.column);
}
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
public ValueWrapper visit(ForRange.Context ctx) {
    ValueWrapper iterableValue = Visit(ctx.iterable);

    if (!(iterableValue instanceof SliceValue)) {
        this.errors.add(new GoliteError(
            "Semantico",
            "La expresion usada en range debe ser un slice, se obtuvo: " + iterableValue.getTypeName(),
            ctx.line,
            ctx.column
        ));
        return defaultVoid;
    }

    SliceValue slice = (SliceValue) iterableValue;

    addSymbol(ctx.indexName, "Variable", "int", currentScope, ctx.line, ctx.column);
    addSymbol(ctx.valueName, "Variable", slice.elementType(), currentScope, ctx.line, ctx.column);

    Enviroment parentEnv = this.enviroment;
    loopDepth++;

    try {
        for (int i = 0; i < slice.values().size(); i++) {
            Enviroment iterationEnv = new Enviroment(parentEnv);
            this.enviroment = iterationEnv;

            iterationEnv.declare(ctx.indexName, new IntValue(i, ctx.line, ctx.column));
            iterationEnv.declare(ctx.valueName, slice.values().get(i));

            try {
                Visit(ctx.body);
            } catch (ContinueException e) {
                // Saltar a la siguiente iteracion
            } catch (BreakException e) {
                break;
            } finally {
                this.enviroment = parentEnv;
            }
        }
    } finally {
        loopDepth--;
        this.enviroment = parentEnv;
    }

    return defaultVoid;
}

@Override
public ValueWrapper visit(RuneLiteral.Context ctx) {
    return new RuneValue(parseRune(ctx.value), ctx.line, ctx.column);
}

private boolean isValidDeclaredType(String type) {
    if (type == null) {
        return false;
    }

    if (type.equals("int")
            || type.equals("float64")
            || type.equals("string")
            || type.equals("bool")
            || type.equals("rune")) {
        return true;
    }

    if (type.startsWith("[]")) {
        String elementType = type.substring(2);
        return isValidDeclaredType(elementType);
    }

    // Tipos struct declarados por el usuario
    if (structTypes.containsKey(type)) {
        return true;
    }

    return false;
}

private ValueWrapper defaultValueForType(String type, int line, int column) {
    if (type.equals("int")) {
        return new IntValue(0, line, column);
    }

    if (type.equals("float64")) {
        return new DecimalValue(0.0, line, column);
    }

    if (type.equals("string")) {
        return new StringValue("\"\"", line, column);
    }

    if (type.equals("bool")) {
        return new BoolValue(false, line, column);
    }

    if (type.equals("rune")) {
        return new RuneValue((char) 0, line, column);
    }

    if (type.startsWith("[]")) {
        String elementType = type.substring(2);
        return new SliceValue(elementType, new java.util.ArrayList<>(), line, column);
    }

    if (structTypes.containsKey(type)) {
        StructTypeDecl.Context structType = structTypes.get(type);
        Map<String, ValueWrapper> values = new LinkedHashMap<>();

        for (StructField field : structType.fields) {
            values.put(field.name, defaultValueForType(field.type, field.line, field.column));
        }

        return new StructValue(type, values, line, column);
    }

    this.errors.add(
        new GoliteError(
            "Semantico",
            "Tipo no reconocido: " + type,
            line,
            column
        )
    );

    return defaultVoid;
}

private ValueWrapper castToDeclaredType(String type, ValueWrapper value, int line, int column) {
    if (value == null) {
        return defaultVoid;
    }

    if (value instanceof NilValue) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "No se puede asignar nil a una variable de tipo '" + type + "'",
                line,
                column
            )
        );

        return defaultVoid;
    }

    /*
     * Caso normal:
     * int con int
     * string con string
     * []int con []int
     * []string con []string
     */
    if (type.equals(value.getTypeName())) {
        return value;
    }

    /*
     * Permitir:
     * var x float64 = 10
     */
    if (type.equals("float64") && value instanceof IntValue v) {
        return new DecimalValue(v.value(), v.line(), v.column());
    }

    this.errors.add(
        new GoliteError(
            "Semantico",
            "No se puede asignar un valor de tipo '" + value.getTypeName()
            + "' a una variable de tipo '" + type + "'",
            line,
            column
        )
    );

    return defaultVoid;
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

    if (ctx.functionName.equals("Len")) {
        ValueWrapper value = Visit(ctx.expression);

        if (value instanceof SliceValue slice) {
            return new IntValue(slice.values().size(), ctx.line, ctx.column);
        }

        if (value instanceof StringValue stringValue) {
            return new IntValue(stringValue.toString().length(), ctx.line, ctx.column);
        }

        this.errors.add(
            new GoliteError(
                "Semantico",
                "La funcion len solo acepta slices o strings, pero recibio '" + value.getTypeName() + "'",
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
    if (loopDepth <= 0 && switchDepth <= 0) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "La sentencia break solo puede usarse dentro de un ciclo o switch",
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
public ValueWrapper visit(MainFunction.Context ctx) {
    Enviroment previous = this.enviroment;
    String previousScope = this.currentScope;

    try {
        this.enviroment = new Enviroment(previous);
        this.currentScope = "main";

        Visit(ctx.body);

    } catch (ReturnException e) {
        // En main, return solo termina la ejecución.
    } finally {
        this.enviroment = previous;
        this.currentScope = previousScope;
    }

    return defaultVoid;
}

@Override
public ValueWrapper visit(SwitchNode.Context ctx) {
    ValueWrapper switchValue = Visit(ctx.expression);

    switchDepth++;

    try {
        // Recorremos los cases uno por uno
        for (CaseNode caseNode : ctx.cases.getCases()) {
            ValueWrapper caseValue = Visit(caseNode.getValue());

            // Si el case coincide, ejecuta SOLO ese case
            if (sameValue(switchValue, caseValue)) {
                try {
                    executeInNewScope(caseNode.getBody());
                } catch (BreakException e) {
                    // break dentro de switch solo sale del switch
                }

                // BREAK IMPLICITO DEL SWITCH
                // Esto evita que ejecute los demás case y default.
                return defaultVoid;
            }
        }

        // Si ningún case coincidió, ejecuta default si existe
        if (ctx.defaultBody != null) {
            try {
                executeInNewScope(ctx.defaultBody);
            } catch (BreakException e) {
                // break dentro de default solo sale del switch
            }
        }

    } finally {
        switchDepth--;
    }

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
public ValueWrapper visit(FunctionDecl.Context ctx) {

    if (statmentsDepth > 1) {
    this.errors.add(
        new GoliteError(
            "Semantico",
            "La funcion '" + ctx.name + "' solo puede declararse en el ambito global",
            ctx.line,
            ctx.column
        )
    );

    return defaultVoid;
}

    if (functions.containsKey(ctx.name)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "La funcion '" + ctx.name + "' ya fue declarada",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    Set<String> paramNames = new HashSet<>();

    for (FunctionParam param : ctx.params) {
        if (paramNames.contains(param.name)) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "El parametro '" + param.name + "' esta repetido en la funcion '" + ctx.name + "'",
                    param.line,
                    param.column
                )
            );

            return defaultVoid;
        }

        paramNames.add(param.name);
    }
    String functionType = ctx.returnType == null ? "void" : ctx.returnType;
String symbolType = ctx.returnType == null ? "Procedimiento" : "Funcion";

addSymbol(ctx.name, symbolType, functionType, "Global", ctx.line, ctx.column);

for (FunctionParam param : ctx.params) {
    addSymbol(param.name, "Parametro", param.type, ctx.name, param.line, param.column);
}
    functions.put(ctx.name, ctx);
    return defaultVoid;
}

@Override
public ValueWrapper visit(FunctionCall.Context ctx) {
    FunctionDecl.Context function = functions.get(ctx.name);

    if (function == null) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "La funcion '" + ctx.name + "' no esta declarada",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    if (ctx.arguments.size() != function.params.size()) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "La funcion '" + ctx.name + "' esperaba "
                + function.params.size() + " argumentos, pero recibio "
                + ctx.arguments.size(),
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    java.util.List<ValueWrapper> argumentValues = new java.util.ArrayList<>();

    for (ASTNODE argument : ctx.arguments) {
        argumentValues.add(Visit(argument));
    }

    Enviroment previous = this.enviroment;
    String previousScope =this.currentScope;

    try {
        this.enviroment = new Enviroment(previous);
        this.currentScope = ctx.name;

        for (int i = 0; i < function.params.size(); i++) {
            FunctionParam param = function.params.get(i);
            ValueWrapper argumentValue = argumentValues.get(i);

            if (!isParameterTypeCompatible(param.type, argumentValue)) {
                this.errors.add(
                    new GoliteError(
                        "Semantico",
                        "El parametro '" + param.name + "' de la funcion '" + ctx.name
                        + "' esperaba tipo '" + param.type
                        + "' pero recibio '" + argumentValue.getTypeName() + "'",
                        param.line,
                        param.column
                    )
                );

                return defaultVoid;
            }

            ValueWrapper valueToDeclare = convertParameterValue(param.type, argumentValue);
            this.enviroment.declare(param.name, valueToDeclare);
        }

        Visit(function.body);

        if (function.returnType != null) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "La funcion '" + ctx.name + "' debe retornar un valor de tipo '" + function.returnType + "'",
                    ctx.line,
                    ctx.column
                )
            );
        }

        return defaultVoid;

    } catch (ReturnException e) {
        ValueWrapper returnedValue = e.getValue();

        if (function.returnType == null) {
            if (!returnedValue.getTypeName().equals("void")) {
                this.errors.add(
                    new GoliteError(
                        "Semantico",
                        "La funcion '" + ctx.name + "' no debe retornar valor",
                        ctx.line,
                        ctx.column
                    )
                );
            }

            return defaultVoid;
        }

        if (!isReturnTypeCompatible(function.returnType, returnedValue)) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "La funcion '" + ctx.name + "' debe retornar '" + function.returnType
                    + "' pero retorno '" + returnedValue.getTypeName() + "'",
                    ctx.line,
                    ctx.column
                )
            );

            return defaultVoid;
        }

        return returnedValue;

    } finally {
    this.enviroment = previous;
    this.currentScope = previousScope;
}
}

@Override
public ValueWrapper visit(SliceLiteral.Context ctx) {
    List<ValueWrapper> values = new ArrayList<>();

    for (ASTNODE node : ctx.values) {
        ValueWrapper value = Visit(node);

        if (!isSliceElementCompatible(ctx.elementType, value)) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "El slice esperaba valores de tipo '" + ctx.elementType
                    + "' pero recibio '" + value.getTypeName() + "'",
                    value.line(),
                    value.column()
                )
            );

            return defaultVoid;
        }

        values.add(convertSliceElement(ctx.elementType, value));
    }

    return new SliceValue(ctx.elementType, values, ctx.line, ctx.column);
}



@Override
public ValueWrapper visit(SliceAccess.Context ctx) {
    ValueWrapper sliceValue = Visit(ctx.slice);
    ValueWrapper indexValue = Visit(ctx.index);
    if (sliceValue instanceof VoidValue) {
    return defaultVoid;
}

if (indexValue instanceof VoidValue) {
    return defaultVoid;
}

    if (!(sliceValue instanceof SliceValue slice)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "Solo se puede acceder por indice a un slice",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    if (!(indexValue instanceof IntValue index)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "El indice del slice debe ser de tipo int",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    int i = index.value();

    if (i < 0 || i >= slice.values().size()) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "Indice fuera de rango. El slice tiene tamaño "
                + slice.values().size() + " y se intento acceder a la posicion " + i,
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    return slice.values().get(i);
}


@Override
public ValueWrapper visit(SliceAssign.Context ctx) {
    ValueWrapper sliceValue = Visit(ctx.slice);
    ValueWrapper indexValue = Visit(ctx.index);
    ValueWrapper newValue = Visit(ctx.value);

    if (!(sliceValue instanceof SliceValue slice)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "Solo se puede asignar por indice a un slice",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    if (!(indexValue instanceof IntValue index)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "El indice del slice debe ser de tipo int",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    int i = index.value();

    if (i < 0 || i >= slice.values().size()) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "Indice fuera de rango. El slice tiene tamaño "
                + slice.values().size() + " y se intento asignar en la posicion " + i,
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    if (!isSliceElementCompatible(slice.elementType(), newValue)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "El slice espera valores de tipo '" + slice.elementType()
                + "' pero se intento asignar '" + newValue.getTypeName() + "'",
                newValue.line(),
                newValue.column()
            )
        );

        return defaultVoid;
    }

    ValueWrapper valueToSet = convertSliceElement(slice.elementType(), newValue);
    slice.values().set(i, valueToSet);

    return defaultVoid;
}


@Override
public ValueWrapper visit(StructTypeDecl.Context ctx) {

    if (statmentsDepth > 1) {
    this.errors.add(new GoliteError(
        "Semantico",
        "El struct '" + ctx.name + "' solo puede declararse en el ambito global",
        ctx.line,
        ctx.column
    ));
    return defaultVoid;
}

    if (structTypes.containsKey(ctx.name)) {
        this.errors.add(new GoliteError(
            "Semantico",
            "El struct '" + ctx.name + "' ya fue declarado",
            ctx.line,
            ctx.column
        ));
        return defaultVoid;
    }

    if (ctx.fields == null || ctx.fields.isEmpty()) {
        this.errors.add(new GoliteError(
            "Semantico",
            "El struct '" + ctx.name + "' debe tener al menos un atributo",
            ctx.line,
            ctx.column
        ));
        return defaultVoid;
    }

    Set<String> fieldNames = new HashSet<>();

    for (StructField field : ctx.fields) {
        if (fieldNames.contains(field.name)) {
            this.errors.add(new GoliteError(
                "Semantico",
                "El atributo '" + field.name + "' esta repetido en el struct '" + ctx.name + "'",
                field.line,
                field.column
            ));
            return defaultVoid;
        }

        if (!isValidDeclaredType(field.type)) {
            this.errors.add(new GoliteError(
                "Semantico",
                "Tipo no reconocido en atributo '" + field.name + "': " + field.type,
                field.line,
                field.column
            ));
            return defaultVoid;
        }

        fieldNames.add(field.name);
    }
    addSymbol(ctx.name, "Struct", ctx.name, "Global", ctx.line, ctx.column);

for (StructField field : ctx.fields) {
    addSymbol(field.name, "Atributo", field.type, ctx.name, field.line, field.column);
}

    structTypes.put(ctx.name, ctx);
    return defaultVoid;
}

@Override
public ValueWrapper visit(StructLiteral.Context ctx) {
    StructTypeDecl.Context structType = structTypes.get(ctx.structName);

    if (structType == null) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "El struct '" + ctx.structName + "' no esta declarado",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    Map<String, ValueWrapper> values = new LinkedHashMap<>();

    // Primero llenamos todos los atributos con valores por defecto
    for (StructField field : structType.fields) {
        values.put(field.name, defaultValueForType(field.type, field.line, field.column));
    }

    Set<String> initializedFields = new HashSet<>();

    // Luego reemplazamos los que vienen en Persona{campo: valor}
    for (StructFieldInit init : ctx.fields) {
        StructField fieldDefinition = findStructField(structType, init.name);

        if (fieldDefinition == null) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "El struct '" + ctx.structName + "' no tiene el atributo '" + init.name + "'",
                    init.line,
                    init.column
                )
            );

            return defaultVoid;
        }

        if (initializedFields.contains(init.name)) {
            this.errors.add(
                new GoliteError(
                    "Semantico",
                    "El atributo '" + init.name + "' fue inicializado mas de una vez",
                    init.line,
                    init.column
                )
            );

            return defaultVoid;
        }

        ValueWrapper evaluated = Visit(init.value);
        ValueWrapper casted = castToDeclaredType(
            fieldDefinition.type,
            evaluated,
            init.line,
            init.column
        );

        if (casted instanceof VoidValue) {
            return defaultVoid;
        }

        values.put(init.name, casted);
        initializedFields.add(init.name);
    }

    return new StructValue(ctx.structName, values, ctx.line, ctx.column);
}

@Override
public ValueWrapper visit(StructAccess.Context ctx) {
    ValueWrapper value = Visit(ctx.struct);

    if (!(value instanceof StructValue structValue)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "Solo se puede acceder a atributos de un struct",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    if (!structValue.fields().containsKey(ctx.fieldName)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "El struct '" + structValue.structName() + "' no tiene el atributo '" + ctx.fieldName + "'",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    return structValue.fields().get(ctx.fieldName);
}

@Override
public ValueWrapper visit(StructAssign.Context ctx) {
    ValueWrapper structValueRaw = Visit(ctx.struct);

    if (!(structValueRaw instanceof StructValue structValue)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "Solo se puede asignar atributos a un struct",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    StructTypeDecl.Context structType = structTypes.get(structValue.structName());

    if (structType == null) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "El struct '" + structValue.structName() + "' no esta declarado",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    StructField fieldDefinition = findStructField(structType, ctx.fieldName);

    if (fieldDefinition == null) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "El struct '" + structValue.structName()
                + "' no tiene el atributo '" + ctx.fieldName + "'",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    ValueWrapper evaluated = Visit(ctx.value);

    ValueWrapper casted = castToDeclaredType(
        fieldDefinition.type,
        evaluated,
        ctx.line,
        ctx.column
    );

    if (casted instanceof VoidValue) {
        return defaultVoid;
    }

    structValue.fields().put(ctx.fieldName, casted);

    return defaultVoid;
}


@Override
public ValueWrapper visit(AppendFunction.Context ctx) {
    ValueWrapper sliceValue = Visit(ctx.slice);
    ValueWrapper valueToAppend = Visit(ctx.value);

    if (!(sliceValue instanceof SliceValue slice)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "La funcion append espera un slice como primer parametro",
                ctx.line,
                ctx.column
            )
        );

        return defaultVoid;
    }

    if (!isSliceElementCompatible(slice.elementType(), valueToAppend)) {
    this.errors.add(
        new GoliteError(
            "Semantico",
            "append esperaba un valor de tipo '" + slice.elementType()
            + "' pero recibio '" + valueToAppend.getTypeName() + "'",
            valueToAppend.line(),
            valueToAppend.column()
        )
    );

    // Evita error en cascada:
    // numeros = append(numeros, "hola")
    // devuelve el mismo slice sin modificarlo.
    return sliceValue;
}

    java.util.List<ValueWrapper> newValues = new java.util.ArrayList<>(slice.values());

    ValueWrapper convertedValue = convertSliceElement(slice.elementType(), valueToAppend);
    newValues.add(convertedValue);

    return new SliceValue(slice.elementType(), newValues, ctx.line, ctx.column);
}

@Override
public ValueWrapper visit(SlicesIndexFunction.Context ctx) {
    ValueWrapper sliceValue = Visit(ctx.slice);
    ValueWrapper searchValue = Visit(ctx.value);

    if (!(sliceValue instanceof SliceValue slice)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "slices.Index espera un slice como primer parametro",
                ctx.line,
                ctx.column
            )
        );

        return new IntValue(-1, ctx.line, ctx.column);
    }

    if (!isSliceElementCompatible(slice.elementType(), searchValue)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "slices.Index esperaba buscar un valor de tipo '" + slice.elementType()
                + "' pero recibio '" + searchValue.getTypeName() + "'",
                searchValue.line(),
                searchValue.column()
            )
        );

        return new IntValue(-1, ctx.line, ctx.column);
    }

    ValueWrapper convertedSearchValue = convertSliceElement(slice.elementType(), searchValue);

    for (int i = 0; i < slice.values().size(); i++) {
        ValueWrapper current = slice.values().get(i);

        if (sameValue(current, convertedSearchValue)) {
            return new IntValue(i, ctx.line, ctx.column);
        }
    }

    return new IntValue(-1, ctx.line, ctx.column);
}


@Override
public ValueWrapper visit(StringsJoinFunction.Context ctx) {
    ValueWrapper sliceValue = Visit(ctx.slice);
    ValueWrapper separatorValue = Visit(ctx.separator);

    if (!(sliceValue instanceof SliceValue slice)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "strings.Join espera un slice como primer parametro",
                ctx.line,
                ctx.column
            )
        );

        return makeStringValue("", ctx.line, ctx.column);
    }

    if (!slice.elementType().equals("string")) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "strings.Join espera un slice de tipo []string, pero recibio '" + slice.getTypeName() + "'",
                ctx.line,
                ctx.column
            )
        );

        return makeStringValue("", ctx.line, ctx.column);
    }

    if (!(separatorValue instanceof StringValue separator)) {
        this.errors.add(
            new GoliteError(
                "Semantico",
                "strings.Join espera un separador de tipo string",
                ctx.line,
                ctx.column
            )
        );

        return makeStringValue("", ctx.line, ctx.column);
    }

    java.util.List<String> parts = new java.util.ArrayList<>();

    for (ValueWrapper value : slice.values()) {
        if (value instanceof StringValue stringValue) {
            parts.add(stringValue.toString());
        }
    }

    String joined = String.join(separator.toString(), parts);

    return makeStringValue(joined, ctx.line, ctx.column);
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

private boolean sameValue(ValueWrapper left, ValueWrapper right) {
    if (left instanceof IntValue l && right instanceof IntValue r) {
        return l.value() == r.value();
    }

    if (left instanceof IntValue l && right instanceof DecimalValue r) {
        return l.value() == r.value();
    }

    if (left instanceof DecimalValue l && right instanceof IntValue r) {
        return l.value() == r.value();
    }

    if (left instanceof DecimalValue l && right instanceof DecimalValue r) {
        return l.value() == r.value();
    }

    if (left instanceof StringValue l && right instanceof StringValue r) {
        return l.toString().equals(r.toString());
    }

    if (left instanceof BoolValue l && right instanceof BoolValue r) {
        return l.value() == r.value();
    }

    if (left instanceof RuneValue l && right instanceof RuneValue r) {
        return l.value() == r.value();
    }

    if (left instanceof RuneValue l && right instanceof IntValue r) {
        return ((int) l.value()) == r.value();
    }

    if (left instanceof IntValue l && right instanceof RuneValue r) {
        return l.value() == ((int) r.value());
    }

    if (left instanceof NilValue && right instanceof NilValue) {
        return true;
    }

    return false;
}

private boolean isReturnTypeCompatible(String expectedType, ValueWrapper value) {
    if (expectedType == null) {
        return value.getTypeName().equals("void");
    }

    if (value == null) {
        return false;
    }

    if (expectedType.equals(value.getTypeName())) {
        return true;
    }

    // Permitimos retornar int en una funcion float64
    if (expectedType.equals("float64") && value.getTypeName().equals("int")) {
        return true;
    }

    return false;
}

private boolean isParameterTypeCompatible(String expectedType, ValueWrapper value) {
    if (expectedType == null || value == null) {
        return false;
    }

    if (expectedType.equals(value.getTypeName())) {
        return true;
    }

    // Permitimos mandar int donde se espera float64
    if (expectedType.equals("float64") && value.getTypeName().equals("int")) {
        return true;
    }

    return false;
}

private ValueWrapper convertParameterValue(String expectedType, ValueWrapper value) {
    if (expectedType.equals("float64") && value instanceof IntValue intValue) {
        return new DecimalValue(intValue.value(), intValue.line(), intValue.column());
    }

    return value;
}



private boolean isSliceElementCompatible(String expectedType, ValueWrapper value) {
    if (expectedType == null || value == null) {
        return false;
    }

    if (expectedType.equals(value.getTypeName())) {
        return true;
    }

    // Permitimos int dentro de []float64
    if (expectedType.equals("float64") && value.getTypeName().equals("int")) {
        return true;
    }

    return false;
}

private ValueWrapper convertSliceElement(String expectedType, ValueWrapper value) {
    if (expectedType.equals("float64") && value instanceof IntValue intValue) {
        return new DecimalValue(intValue.value(), intValue.line(), intValue.column());
    }

    return value;
}


private StringValue makeStringValue(String text, int line, int column) {
    String escaped = text
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\t", "\\t")
        .replace("\r", "\\r");

    return new StringValue("\"" + escaped + "\"", line, column);
}


private StructField findStructField(StructTypeDecl.Context structType, String fieldName) {
    for (StructField field : structType.fields) {
        if (field.name.equals(fieldName)) {
            return field;
        }
    }

    return null;
}

private void addSymbol(String id, String symbolType, String dataType, String scope, int line, int column) {
    if (id == null || symbolType == null || dataType == null || scope == null) {
        return;
    }

    String key = id + "|" + symbolType + "|" + dataType + "|" + scope + "|" + line + "|" + column;

    if (symbolKeys.contains(key)) {
        return;
    }

    symbolKeys.add(key);
    symbols.add(new SymbolReport(id, symbolType, dataType, scope, line, column));
}

}

    
