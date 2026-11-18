package com.equationsolver.equations.core;

/** Visitor over the expression tree (Visitor pattern): new operations need no change to the nodes. */
public interface NodeVisitor<R> {
    R visitNumber(NumberNode node);

    R visitVariable(VariableNode node);

    R visitUnary(UnaryNode node);

    R visitBinary(BinaryNode node);
}
