package com.equationsolver.equations.core;

/** Leaf naming a variable whose value is supplied at evaluation time. */
public record VariableNode(String name) implements ExpressionNode {

    @Override
    public <R> R accept(NodeVisitor<R> visitor) {
        return visitor.visitVariable(this);
    }
}
