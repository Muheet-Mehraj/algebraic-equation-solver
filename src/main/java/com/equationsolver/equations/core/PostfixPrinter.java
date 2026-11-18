package com.equationsolver.equations.core;

/** Renders the tree as space-separated postfix text (post-order traversal), e.g. "3 x * 2 y * + z -". */
public final class PostfixPrinter implements NodeVisitor<String> {

    public static String print(ExpressionNode root) {
        return root.accept(new PostfixPrinter());
    }

    @Override
    public String visitNumber(NumberNode node) {
        return NumberFormatter.format(node.value());
    }

    @Override
    public String visitVariable(VariableNode node) {
        return node.name();
    }

    @Override
    public String visitUnary(UnaryNode node) {
        return node.operand().accept(this) + " " + node.operator().symbol();
    }

    @Override
    public String visitBinary(BinaryNode node) {
        return node.left().accept(this) + " " + node.right().accept(this) + " " + node.operator().symbol();
    }
}
