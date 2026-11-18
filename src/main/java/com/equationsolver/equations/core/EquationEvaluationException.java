package com.equationsolver.equations.core;

/** The equation is valid but cannot be computed for the supplied values (e.g. division by zero). */
public class EquationEvaluationException extends EquationException {
    public EquationEvaluationException(String message) {
        super(message);
    }
}
