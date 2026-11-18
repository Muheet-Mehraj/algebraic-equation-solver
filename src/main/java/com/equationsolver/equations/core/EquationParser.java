package com.equationsolver.equations.core;

import java.util.List;

/** Facade: equation text -> tokens -> postfix -> expression tree. */
public final class EquationParser {

    public static final int MAX_LENGTH = 500;

    private final Tokenizer tokenizer = new Tokenizer();
    private final InfixToPostfixConverter converter = new InfixToPostfixConverter();
    private final ExpressionTreeBuilder builder = new ExpressionTreeBuilder();

    public ExpressionNode parse(String equation) {
        if (equation != null && equation.length() > MAX_LENGTH) {
            throw new InvalidEquationException("Equation is too long (max " + MAX_LENGTH + " characters)");
        }
        List<Token> postfix = converter.convert(tokenizer.tokenize(equation));
        return builder.build(postfix);
    }
}
