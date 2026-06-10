package com.olc1.ast;

import com.olc1.visitor.Visitor;

public interface ASTNODE {
    <T> T accept(Visitor<T> visitor);
}
