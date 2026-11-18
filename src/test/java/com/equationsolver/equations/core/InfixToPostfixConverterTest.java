package com.equationsolver.equations.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InfixToPostfixConverterTest {

    private final EquationParser parser = new EquationParser();

    private String postfix(String equation) {
        return PostfixPrinter.print(parser.parse(equation));
    }

    private void assertInvalid(String equation, String messagePart) {
        InvalidEquationException e = assertThrows(InvalidEquationException.class, () -> parser.parse(equation), equation);
        assertTrue(e.getMessage().toLowerCase().contains(messagePart.toLowerCase()),
                "message '" + e.getMessage() + "' should contain '" + messagePart + "'");
    }

    @Test
    void examplesFromTheAssignment() {
        assertEquals("3 x * 2 y * + z -", postfix("3x + 2y - z"));
        assertEquals("x 2 ^ y 2 ^ + 4 -", postfix("x^2 + y^2 - 4"));
    }

    @Test
    void respectsPrecedence() {
        assertEquals("a b c * +", postfix("a + b * c"));
        assertEquals("a b * c +", postfix("a * b + c"));
    }

    @Test
    void additionAndSubtractionAreLeftAssociative() {
        assertEquals("a b - c -", postfix("a - b - c"));
        assertEquals("a b / c /", postfix("a / b / c"));
    }

    @Test
    void powerIsRightAssociative() {
        assertEquals("2 3 2 ^ ^", postfix("2^3^2"));
    }

    @Test
    void parenthesesOverridePrecedence() {
        assertEquals("a b + c *", postfix("(a + b) * c"));
        assertEquals("a b c + d - *", postfix("a * ((b + c) - d)"));
    }

    @Test
    void unaryMinusBindsLooserThanPowerButTighterThanMultiplication() {
        assertEquals("x 2 ^ neg", postfix("-x^2"));
        assertEquals("x neg 2 ^", postfix("(-x)^2"));
        assertEquals("2 x neg *", postfix("2 * -x"));
        assertEquals("a neg b +", postfix("-a + b"));
        assertEquals("x neg neg", postfix("--x"));
    }

    @Test
    void implicitMultiplication() {
        assertEquals("2 x *", postfix("2x"));
        assertEquals("3 x 2 ^ *", postfix("3x^2"));
        assertEquals("2 x 1 + *", postfix("2(x+1)"));
        assertEquals("a b + c d + *", postfix("(a+b)(c+d)"));
        assertEquals("x 2 ^ y *", postfix("x^2y"));
        assertEquals("x y *", postfix("x y"));
    }

    @Test
    void rejectsEmptyOrMalformedStructure() {
        assertInvalid("", "empty");
        assertInvalid("()", "empty parentheses");
        assertInvalid("3 +", "incomplete");
        assertInvalid("* 3", "operand was expected");
        assertInvalid("+3", "operand was expected");
        assertInvalid("3 + * 2", "operand was expected");
        assertInvalid("3 ** 2", "operand was expected");
        assertInvalid("3 4", "missing operator");
        assertInvalid("(3 + 4", "unmatched '('");
        assertInvalid("3 + 4)", "unmatched ')'");
        assertInvalid("(3 + )", "missing operand before ')'");
        assertInvalid("2 x $", "unexpected character");
    }

    @Test
    void rejectsOverlongEquation() {
        assertInvalid("x+".repeat(300) + "x", "too long");
    }

    @Test
    void rejectsNumbersTooLargeForADouble() {
        assertInvalid("9".repeat(400), "too large");
    }
}
