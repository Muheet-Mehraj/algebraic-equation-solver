package com.equationsolver.equations.core;

/** A node of the expression (postfix) tree: operators are parents, operands are leaves/children. */
public sealed interface ExpressionNode permits NumberNode, VariableNode, UnaryNode, BinaryNode {
    <R> R accept(NodeVisitor<R> visitor);
}
