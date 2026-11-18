package com.equationsolver.equations.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InfixPrinterTest {

    private final EquationParser parser = new EquationParser();

    private String roundTrip(String equation) {
        return InfixPrinter.print(parser.parse(equation));
    }

    @Test
    void reconstructsTheAssignmentExamples() {
        assertEquals("3x + 2y - z", roundTrip("3x + 2y - z"));
        assertEquals("x^2 + y^2 - 4", roundTrip("x^2 + y^2 - 4"));
    }

    @Test
    void normalisesWhitespaceAndRedundantParentheses() {
        assertEquals("a + b * c", roundTrip("  a+(b*c) "));
        assertEquals("3x", roundTrip("3*x"));
        assertEquals("1.5", roundTrip("1.50"));
    }

    @Test
    void keepsParenthesesThatChangeMeaning() {
        assertEquals("(a + b) * c", roundTrip("(a+b)*c"));
        assertEquals("a - (b - c)", roundTrip("a-(b-c)"));
        assertEquals("a / (b * c)", roundTrip("a/(b*c)"));
        assertEquals("(2^3)^2", roundTrip("(2^3)^2"));
        assertEquals("2^3^2", roundTrip("2^(3^2)"));
        assertEquals("(3x)^2", roundTrip("(3x)^2"));
    }

    @Test
    void printsUnaryMinus() {
        assertEquals("-x^2", roundTrip("-x^2"));
        assertEquals("(-x)^2", roundTrip("(-x)^2"));
        assertEquals("-(a + b)", roundTrip("-(a+b)"));
        assertEquals("-(-x)", roundTrip("--x"));
        assertEquals("a * -b", roundTrip("a * -b"));
    }

    @Test
    void printedFormAlwaysReparsesToTheSameTree() {
        String[] samples = {
                "3x + 2y - z", "x^2 + y^2 - 4", "-(a + b) * c / (d - e)", "2^3^2 - (2^3)^2",
                "((a))", "a - (b + c) - d", "-x^-2", "2(x + 1)(x - 1)", "1.25x^3 / y"
        };
        for (String sample : samples) {
            ExpressionNode original = parser.parse(sample);
            ExpressionNode reparsed = parser.parse(InfixPrinter.print(original));
            assertEquals(original, reparsed, "round trip of " + sample);
        }
    }
}
