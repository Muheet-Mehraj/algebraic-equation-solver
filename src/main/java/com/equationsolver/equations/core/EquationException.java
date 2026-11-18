package com.equationsolver.equations.core;

/** Base type for all domain errors raised by the equation engine. */
public abstract class EquationException extends RuntimeException {
    protected EquationException(String message) {
        super(message);
    }
}
