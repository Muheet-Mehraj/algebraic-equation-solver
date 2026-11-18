package com.equationsolver.equations.core;

/** Operator node with a left and a right child. */
public record BinaryNode(Operator operator, ExpressionNode left, ExpressionNode right) implements ExpressionNode {

    @Override
    public <R> R accept(NodeVisitor<R> visitor) {
        return visitor.visitBinary(this);
    }
}
