package com.equationsolver.equations.core;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/** Builds the expression tree from postfix tokens using a single operand stack. */
public final class ExpressionTreeBuilder {

    public ExpressionNode build(List<Token> postfix) {
        Deque<ExpressionNode> stack = new ArrayDeque<>();
        for (Token token : postfix) {
            switch (token.type()) {
                case NUMBER -> stack.push(new NumberNode(new BigDecimal(token.text())));
                case VARIABLE -> stack.push(new VariableNode(token.text()));
                case OPERATOR -> {
                    Operator op = token.operator();
                    if (stack.size() < op.arity()) {
                        throw new InvalidEquationException(
                                "Operator '" + token.text() + "' at position " + token.position() + " is missing an operand");
                    }
                    if (op.arity() == 1) {
                        stack.push(new UnaryNode(op, stack.pop()));
                    } else {
                        ExpressionNode right = stack.pop();
                        ExpressionNode left = stack.pop();
                        stack.push(new BinaryNode(op, left, right));
                    }
                }
                default -> throw new InvalidEquationException("Unexpected token '" + token.text() + "' in postfix form");
            }
        }
        if (stack.size() != 1) {
            throw new InvalidEquationException("Equation is not a single well-formed expression");
        }
        return stack.pop();
    }
}
