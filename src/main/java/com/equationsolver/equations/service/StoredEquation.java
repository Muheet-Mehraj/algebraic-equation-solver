package com.equationsolver.equations.service;

import com.equationsolver.equations.core.ExpressionNode;
import com.equationsolver.equations.core.InfixPrinter;
import com.equationsolver.equations.core.PostfixPrinter;

/**
 * A stored equation. Only the expression tree is kept; the displayed infix and postfix forms
 * are always reconstructed from it by traversal.
 */
public record StoredEquation(String id, ExpressionNode root) {

    public String infix() {
        return InfixPrinter.print(root);
    }

    public String postfix() {
        return PostfixPrinter.print(root);
    }
}
