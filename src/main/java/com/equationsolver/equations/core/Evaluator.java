package com.equationsolver.equations.core;

import java.util.List;
import java.util.Map;

/** Evaluates an expression tree for a set of variable values (post-order walk). */
public final class Evaluator implements NodeVisitor<Double> {

    private final Map<String, Double> variables;

    private Evaluator(Map<String, Double> variables) {
        this.variables = variables;
    }

    /**
     * @throws MissingVariableException     if any variable used by the tree has no value
     * @throws EquationEvaluationException  on division by zero or a non-finite result
     */
    public static double evaluate(ExpressionNode root, Map<String, Double> variables) {
        List<String> missing = VariableCollector.collect(root).stream()
                .filter(name -> variables.get(name) == null)
                .toList();
        if (!missing.isEmpty()) {
            throw new MissingVariableException(missing);
        }
        return root.accept(new Evaluator(variables));
    }

    @Override
    public Double visitNumber(NumberNode node) {
        return node.value().doubleValue();
    }

    @Override
    public Double visitVariable(VariableNode node) {
        double value = variables.get(node.name());
        if (!Double.isFinite(value)) {
            throw new EquationEvaluationException("Value for variable '" + node.name() + "' is not a finite number");
        }
        return value;
    }

    @Override
    public Double visitUnary(UnaryNode node) {
        return checkFinite(node.operator().apply(node.operand().accept(this)), node.operator());
    }

    @Override
    public Double visitBinary(BinaryNode node) {
        double left = node.left().accept(this);
        double right = node.right().accept(this);
        return checkFinite(node.operator().apply(left, right), node.operator());
    }

    private static double checkFinite(double result, Operator operator) {
        if (!Double.isFinite(result)) {
            throw new EquationEvaluationException(
                    "Operation '" + operator.symbol() + "' produced a result that is not a finite number");
        }
        return result;
    }
}
