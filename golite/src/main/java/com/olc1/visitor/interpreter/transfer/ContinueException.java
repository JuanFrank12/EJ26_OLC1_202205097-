package com.olc1.visitor.interpreter.transfer;

public class ContinueException extends RuntimeException {
    public ContinueException() {
        super("Continue Statments");
    }
}