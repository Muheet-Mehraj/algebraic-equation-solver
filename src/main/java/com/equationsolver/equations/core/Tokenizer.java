package com.equationsolver.equations.core;

import com.equationsolver.equations.core.Token.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits equation text into tokens.
 *
 * <p>Numbers are decimal ({@code 12}, {@code 1.5}, {@code .5}). A variable is a letter or underscore
 * followed by letters, digits or underscores, so {@code xy} is ONE variable called "xy"; write
 * {@code x*y} or {@code x y} for a product.
 */
public final class Tokenizer {

    public List<Token> tokenize(String input) {
        if (input == null || input.isBlank()) {
            throw new InvalidEquationException("Equation must not be empty");
        }
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        int n = input.length();
        while (i < n) {
            char c = input.charAt(i);
            int column = i + 1;
            if (Character.isWhitespace(c)) {
                i++;
            } else if (isAsciiDigit(c) || (c == '.' && i + 1 < n && isAsciiDigit(input.charAt(i + 1)))) {
                int start = i;
                while (i < n && isAsciiDigit(input.charAt(i))) {
                    i++;
                }
                if (i < n && input.charAt(i) == '.') {
                    i++;
                    while (i < n && isAsciiDigit(input.charAt(i))) {
                        i++;
                    }
                }
                if (i < n && input.charAt(i) == '.') {
                    throw new InvalidEquationException("Malformed number at position " + column);
                }
                tokens.add(Token.of(TokenType.NUMBER, input.substring(start, i), column));
            } else if (isAsciiLetter(c) || c == '_') {
                int start = i;
                while (i < n && (isAsciiLetter(input.charAt(i)) || isAsciiDigit(input.charAt(i)) || input.charAt(i) == '_')) {
                    i++;
                }
                tokens.add(Token.of(TokenType.VARIABLE, input.substring(start, i), column));
            } else if (c == '(') {
                tokens.add(Token.of(TokenType.LEFT_PAREN, "(", column));
                i++;
            } else if (c == ')') {
                tokens.add(Token.of(TokenType.RIGHT_PAREN, ")", column));
                i++;
            } else if (Operator.binaryFor(c).isPresent()) {
                tokens.add(Token.of(TokenType.OPERATOR, String.valueOf(c), column));
                i++;
            } else {
                throw new InvalidEquationException("Unexpected character '" + c + "' at position " + column);
            }
        }
        return tokens;
    }

    private static boolean isAsciiDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isAsciiLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
}
