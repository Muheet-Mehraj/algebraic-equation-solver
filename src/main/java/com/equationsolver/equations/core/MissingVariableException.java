package com.equationsolver.equations.core;

import java.util.List;

/** One or more variables used by the equation were not supplied for evaluation. */
public class MissingVariableException extends EquationException {
    private final List<String> missing;

    public MissingVariableException(List<String> missing) {
        super("Missing values for variables: " + String.join(", ", missing));
        this.missing = List.copyOf(missing);
    }

    public List<String> getMissing() {
        return missing;
    }
}
