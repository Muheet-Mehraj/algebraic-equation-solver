package com.equationsolver.equations.core;

import com.equationsolver.equations.core.Token.TokenType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Converts infix tokens to postfix (Reverse Polish) order with the shunting-yard algorithm,
 * validating syntax along the way.
 *
 * <ul>
 *   <li>Implicit multiplication is supported: {@code 3x}, {@code 2(x+1)}, {@code (a)(b)}.</li>
 *   <li>Unary minus is supported ({@code -x}, {@code 2*-3}); unary plus is rejected.</li>
 *   <li>{@code ^} is right-associative and binds tighter than unary minus: {@code -x^2 = -(x^2)}.</li>
 * </ul>
 */
public final class InfixToPostfixConverter {

    public List<Token> convert(List<Token> tokens) {
        if (tokens.isEmpty()) {
            throw new InvalidEquationException("Equation must not be empty");
        }
        List<Token> output = new ArrayList<>();
        Deque<Token> stack = new ArrayDeque<>();
        boolean expectOperand = true;
        Token previous = null;

        for (Token token : tokens) {
            switch (token.type()) {
                case NUMBER, VARIABLE -> {
                    if (!expectOperand) {
                        if (previous.type() == TokenType.NUMBER && token.type() == TokenType.NUMBER) {
                            throw new InvalidEquationException(
                                    "Unexpected number '" + token.text() + "' at position " + token.position()
                                            + ": missing operator between numbers");
                        }
                        pushBinary(Token.operator(Operator.MULTIPLY, token.position()), stack, output);
                    }
                    output.add(token);
                    expectOperand = false;
                }
                case LEFT_PAREN -> {
                    if (!expectOperand) {
                        pushBinary(Token.operator(Operator.MULTIPLY, token.position()), stack, output);
                    }
                    stack.push(token);
                    expectOperand = true;
                }
                case RIGHT_PAREN -> {
                    if (expectOperand) {
                        throw new InvalidEquationException(previous.type() == TokenType.LEFT_PAREN
                                ? "Empty parentheses at position " + previous.position()
                                : "Missing operand before ')' at position " + token.position());
                    }
                    while (!stack.isEmpty() && stack.peek().type() != TokenType.LEFT_PAREN) {
                        output.add(stack.pop());
                    }
                    if (stack.isEmpty()) {
                        throw new InvalidEquationException("Unmatched ')' at position " + token.position());
                    }
                    stack.pop();
                    expectOperand = false;
                }
                case OPERATOR -> {
                    char symbol = token.text().charAt(0);
                    if (expectOperand) {
                        if (symbol != '-') {
                            throw new InvalidEquationException("Unexpected operator '" + symbol
                                    + "' at position " + token.position() + ": an operand was expected");
                        }
                        // Prefix operators never pop anything; they simply wait for their operand.
                        stack.push(Token.operator(Operator.NEGATE, token.position()));
                    } else {
                        pushBinary(Token.operator(Operator.binaryFor(symbol).orElseThrow(), token.position()), stack, output);
                        expectOperand = true;
                    }
                }
            }
            previous = token;
        }

        if (expectOperand) {
            throw new InvalidEquationException(
                    "Equation is incomplete: an operand is missing after position " + previous.position());
        }
        while (!stack.isEmpty()) {
            Token top = stack.pop();
            if (top.type() == TokenType.LEFT_PAREN) {
                throw new InvalidEquationException("Unmatched '(' at position " + top.position());
            }
            output.add(top);
        }
        return output;
    }

    private static void pushBinary(Token incoming, Deque<Token> stack, List<Token> output) {
        Operator op = incoming.operator();
        while (!stack.isEmpty() && stack.peek().type() == TokenType.OPERATOR) {
            Operator top = stack.peek().operator();
            boolean popTop = top.precedence() > op.precedence()
                    || (top.precedence() == op.precedence() && !op.isRightAssociative());
            if (!popTop) {
                break;
            }
            output.add(stack.pop());
        }
        stack.push(incoming);
    }
}
