package com.olc1;

import analisis.parser;
import analisis.Scanner;
import java.io.BufferedReader;
import java.io.StringReader;

public class Main {
    public static void main(String[] args) {
        try {
            String texto = "imprimir(1+2+3);imprimir(-1+2*2+9/4);";
            Scanner s = new Scanner(new BufferedReader(new StringReader(texto)));
            parser p = new parser(s);
            var resultado = p.parse().value;
            System.out.println(resultado);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}