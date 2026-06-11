package com.olc1.visitor.interpreter;

import java.util.HashMap;
import java.util.Map;

import javax.management.RuntimeErrorException;
import com.olc1.visitor.interpreter.value.DecimalValue;
import com.olc1.visitor.interpreter.value.IntValue;
import com.olc1.visitor.interpreter.value.ValueWrapper;


public class Enviroment {
    private final Map<String, ValueWrapper> variables;
    private final Enviroment parent;

    public Enviroment() {
        variables = new HashMap<>();
        this.parent = null;
    }

    public Enviroment(Enviroment parent) {
        variables = new HashMap<>();
        this.parent = parent;
    }

    public ValueWrapper declare(String name, ValueWrapper value) {
        if (variables.containsKey(name)) {
            throw new RuntimeException("Variable '" + name + "' ya declarada en este ámbito");
        }
        variables.put(name, value);
        return value;
    }

    public ValueWrapper get(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        } else if (parent != null) {
            return parent.get(name);
        } else {
            throw new RuntimeException("Variable '" + name + "' no declarada");
        }
    }

     public ValueWrapper set(String name, ValueWrapper value) {
        if (variables.containsKey(name)) {
            ValueWrapper oldValue = variables.get(name);

            // Permitir int -> float64
            if (oldValue instanceof DecimalValue && value instanceof IntValue v) {
                DecimalValue converted = new DecimalValue(v.value(), v.line(), v.column());
                variables.put(name, converted);
                return converted;
            }

            // Bloquear cambio de tipo
            if (!oldValue.getTypeName().equals(value.getTypeName())) {
                throw new RuntimeException(
                    "No se puede asignar un valor de tipo '" + value.getTypeName()
                    + "' a la variable '" + name
                    + "' de tipo '" + oldValue.getTypeName() + "'"
                );
            }

            variables.put(name, value);
            return value;

        } else if (parent != null) {
            return parent.set(name, value);

        } else {
            throw new RuntimeException("Variable '" + name + "' no declarada");
        }
    }
}