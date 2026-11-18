package com.equationsolver.equations.core;

import java.math.BigDecimal;

/** Leaf holding a numeric literal. */
public record NumberNode(BigDecimal value) implements ExpressionNode {

    public NumberNode {
        if (!Double.isFinite(value.doubleValue())) {
            throw new InvalidEquationException("Number is too large: " + value.toPlainString());
        }
    }

    @Override
    public <R> R accept(NodeVisitor<R> visitor) {
        return visitor.visitNumber(this);
    }
}
