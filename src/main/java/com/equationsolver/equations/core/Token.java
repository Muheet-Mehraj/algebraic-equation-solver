package com.equationsolver.equations.core;

/**
 * A lexical unit. {@code operator} is only set for OPERATOR tokens that have been resolved
 * to a concrete operator (the tokenizer leaves it null because '-' may be unary or binary).
 *
 * @param position 1-based column in the source text, used in error messages
 */
public record Token(TokenType type, String text, int position, Operator operator) {

    public enum TokenType { NUMBER, VARIABLE, OPERATOR, LEFT_PAREN, RIGHT_PAREN }

    public static Token of(TokenType type, String text, int position) {
        return new Token(type, text, position, null);
    }

    public static Token operator(Operator operator, int position) {
        return new Token(TokenType.OPERATOR, operator.symbol(), position, operator);
    }
}
