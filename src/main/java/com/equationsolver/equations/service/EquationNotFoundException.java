package com.equationsolver.equations.service;

import com.equationsolver.equations.core.EquationException;

public class EquationNotFoundException extends EquationException {
    public EquationNotFoundException(String id) {
        super("Equation with id '" + id + "' was not found");
    }
}
