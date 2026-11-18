package com.equationsolver.equations.core;

/** The equation text is syntactically invalid. */
public class InvalidEquationException extends EquationException {
    public InvalidEquationException(String message) {
        super(message);
    }
}
