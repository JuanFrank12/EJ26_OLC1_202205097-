package com.olc1;

import java.io.BufferedReader;
import java.io.StringReader;

import analisis.Lexer;
import analisis.parser;

import com.olc1.ast.ASTNODE;
import com.olc1.visitor.interpreter.InterpreterVisitor;

public class Main {
    public static void main(String[] args) {
        try {
            String texto = "dos = 2; imprimir(5 * dos); if (true) { imprimir(20); } if (false) { imprimir(30); }";

            Lexer s = new Lexer(new BufferedReader(new StringReader(texto)));
            parser p = new parser(s);

            ASTNODE ast = (ASTNODE) p.parse().value;

            InterpreterVisitor interpreter = new InterpreterVisitor();
            ast.accept(interpreter);

            System.out.print(interpreter.output);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}