package com.equationsolver.equations.core;

import java.util.Optional;

/**
 * Operators supported in equations. Each constant carries its own precedence, associativity,
 * arity and arithmetic (Strategy pattern), so no other class needs a switch over operators.
 */
public enum Operator {
    ADD("+", 1, false, 2) {
        @Override
        public double apply(double... operands) {
            return operands[0] + operands[1];
        }
    },
    SUBTRACT("-", 1, false, 2) {
        @Override
        public double apply(double... operands) {
            return operands[0] - operands[1];
        }
    },
    MULTIPLY("*", 2, false, 2) {
        @Override
        public double apply(double... operands) {
            return operands[0] * operands[1];
        }
    },
    DIVIDE("/", 2, false, 2) {
        @Override
        public double apply(double... operands) {
            if (operands[1] == 0.0) {
                throw new EquationEvaluationException("Division by zero");
            }
            return operands[0] / operands[1];
        }
    },
    /** Unary minus. Binds tighter than * and / but looser than ^, so -x^2 means -(x^2). */
    NEGATE("neg", 3, true, 1) {
        @Override
        public double apply(double... operands) {
            return -operands[0];
        }
    },
    POWER("^", 4, true, 2) {
        @Override
        public double apply(double... operands) {
            return Math.pow(operands[0], operands[1]);
        }
    };

    private final String symbol;
    private final int precedence;
    private final boolean rightAssociative;
    private final int arity;

    Operator(String symbol, int precedence, boolean rightAssociative, int arity) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.rightAssociative = rightAssociative;
        this.arity = arity;
    }

    public String symbol() {
        return symbol;
    }

    public int precedence() {
        return precedence;
    }

    public boolean isRightAssociative() {
        return rightAssociative;
    }

    public int arity() {
        return arity;
    }

    /** Applies the operator to already evaluated operands. */
    public abstract double apply(double... operands);

    /** Looks up a binary operator by its infix character. Unary minus is decided by the parser. */
    public static Optional<Operator> binaryFor(char c) {
        return switch (c) {
            case '+' -> Optional.of(ADD);
            case '-' -> Optional.of(SUBTRACT);
            case '*' -> Optional.of(MULTIPLY);
            case '/' -> Optional.of(DIVIDE);
            case '^' -> Optional.of(POWER);
            default -> Optional.empty();
        };
    }
}
