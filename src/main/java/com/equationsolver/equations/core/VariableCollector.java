package com.equationsolver.equations.core;

import java.util.LinkedHashSet;
import java.util.Set;

/** Collects the distinct variable names used by a tree, in order of first appearance. */
public final class VariableCollector implements NodeVisitor<Void> {

    private final Set<String> names = new LinkedHashSet<>();

    public static Set<String> collect(ExpressionNode root) {
        VariableCollector collector = new VariableCollector();
        root.accept(collector);
        return collector.names;
    }

    @Override
    public Void visitNumber(NumberNode node) {
        return null;
    }

    @Override
    public Void visitVariable(VariableNode node) {
        names.add(node.name());
        return null;
    }

    @Override
    public Void visitUnary(UnaryNode node) {
        return node.operand().accept(this);
    }

    @Override
    public Void visitBinary(BinaryNode node) {
        node.left().accept(this);
        return node.right().accept(this);
    }
}
