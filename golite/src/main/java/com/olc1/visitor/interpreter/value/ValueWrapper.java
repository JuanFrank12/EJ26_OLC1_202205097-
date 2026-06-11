package com.olc1.visitor.interpreter.value;



public sealed interface ValueWrapper
    permits IntValue, DecimalValue, VoidValue, BoolValue, StringValue, NilValue, RuneValue {
    
    int line();
    int column();
    String getTypeName();
}

