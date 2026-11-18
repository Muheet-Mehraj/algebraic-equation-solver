package com.equationsolver.equations.core;

/** Operator node with a single child (unary minus). */
public record UnaryNode(Operator operator, ExpressionNode operand) implements ExpressionNode {

    @Override
    public <R> R accept(NodeVisitor<R> visitor) {
        return visitor.visitUnary(this);
    }
}
