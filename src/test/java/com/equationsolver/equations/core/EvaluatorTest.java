package com.equationsolver.equations.core;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvaluatorTest {

    private final EquationParser parser = new EquationParser();

    private double eval(String equation, Map<String, Double> vars) {
        return Evaluator.evaluate(parser.parse(equation), vars);
    }

    @Test
    void assignmentExample() {
        assertEquals(11.0, eval("3x + 2y - z", Map.of("x", 2.0, "y", 3.0, "z", 1.0)), 1e-9);
        assertEquals(9.0, eval("x^2 + y^2 - 4", Map.of("x", 2.0, "y", 3.0)), 1e-9);
    }

    @Test
    void constantsNeedNoVariables() {
        assertEquals(14.0, eval("2 + 3 * 4", Map.of()), 1e-9);
        assertEquals(20.0, eval("(2 + 3) * 4", Map.of()), 1e-9);
        assertEquals(0.5, eval("1 / 2", Map.of()), 1e-9);
    }

    @Test
    void operatorSemantics() {
        assertEquals(512.0, eval("2^3^2", Map.of()), 1e-9);
        assertEquals(64.0, eval("(2^3)^2", Map.of()), 1e-9);
        assertEquals(-9.0, eval("-3^2", Map.of()), 1e-9);
        assertEquals(9.0, eval("(-3)^2", Map.of()), 1e-9);
        assertEquals(-6.0, eval("2 * -3", Map.of()), 1e-9);
        assertEquals(5.0, eval("--5", Map.of()), 1e-9);
        assertEquals(0.25, eval("2^-2", Map.of()), 1e-9);
        assertEquals(1.0, eval("0^0", Map.of()), 1e-9);
        assertEquals(1.5, eval("3 / 2", Map.of()), 1e-9);
    }

    @Test
    void complexEquation() {
        // 2(x+1)(x-1) + a/b - (c - d)^2 at x=3, a=9, b=3, c=5, d=1  -> 16 + 3 - 16 = 3
        double result = eval("2(x+1)(x-1) + a/b - (c-d)^2",
                Map.of("x", 3.0, "a", 9.0, "b", 3.0, "c", 5.0, "d", 1.0));
        assertEquals(3.0, result, 1e-9);
    }

    @Test
    void decimalsAndNegativeValues() {
        assertEquals(-1.25, eval("1.5x + 0.25", Map.of("x", -1.0)), 1e-9);
        assertEquals(0.3, eval("0.1 + 0.2", Map.of()), 1e-9);
    }

    @Test
    void extraVariablesAreIgnored() {
        assertEquals(4.0, eval("2x", Map.of("x", 2.0, "unused", 99.0)), 1e-9);
    }

    @Test
    void reportsAllMissingVariablesInOrder() {
        MissingVariableException e = assertThrows(MissingVariableException.class,
                () -> eval("3x + 2y - z + x", Map.of("y", 1.0)));
        assertEquals(List.of("x", "z"), e.getMissing());
        assertTrue(e.getMessage().contains("x, z"));
    }

    @Test
    void divisionByZeroIsRejected() {
        EquationEvaluationException e = assertThrows(EquationEvaluationException.class, () -> eval("1 / x", Map.of("x", 0.0)));
        assertTrue(e.getMessage().toLowerCase().contains("division by zero"));
        assertThrows(EquationEvaluationException.class, () -> eval("1 / (x - x)", Map.of("x", 5.0)));
    }

    @Test
    void nonFiniteResultsAreRejected() {
        assertThrows(EquationEvaluationException.class, () -> eval("(-8)^0.5", Map.of()));
        assertThrows(EquationEvaluationException.class, () -> eval("10^400", Map.of()));
        assertThrows(EquationEvaluationException.class, () -> eval("0^-1", Map.of()));
        assertThrows(EquationEvaluationException.class, () -> eval("x", Map.of("x", Double.POSITIVE_INFINITY)));
        assertThrows(EquationEvaluationException.class, () -> eval("x", Map.of("x", Double.NaN)));
    }

    @Test
    void variableCollectorListsDistinctNamesInOrder() {
        assertEquals(List.copyOf(Set.of("b")), List.copyOf(VariableCollector.collect(parser.parse("b + b * 2"))));
        assertEquals(List.of("y", "x", "z"), List.copyOf(VariableCollector.collect(parser.parse("y + x * (z - y)"))));
    }
}
