package com.equationsolver.equations.core;

/**
 * Reconstructs readable infix text from the tree using minimal parentheses.
 * A number times a variable (or variable power) is shown in coefficient form, so the tree
 * for "3x + 2y - z" prints back as "3x + 2y - z".
 */
public final class InfixPrinter implements NodeVisitor<String> {

    public static String print(ExpressionNode root) {
        return root.accept(new InfixPrinter());
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
        ExpressionNode child = node.operand();
        String text = child.accept(this);
        boolean needsParens = precedence(child) <= node.operator().precedence() && !(child instanceof NumberNode);
        return "-" + (needsParens ? "(" + text + ")" : text);
    }

    @Override
    public String visitBinary(BinaryNode node) {
        Operator op = node.operator();
        String left = wrap(node.left(), op, true);
        String right = wrap(node.right(), op, false);
        if (isCoefficientForm(node)) {
            return left + right;
        }
        return op == Operator.POWER ? left + "^" + right : left + " " + op.symbol() + " " + right;
    }

    private static boolean isCoefficientForm(BinaryNode node) {
        if (node.operator() != Operator.MULTIPLY || !(node.left() instanceof NumberNode)) {
            return false;
        }
        ExpressionNode right = node.right();
        return right instanceof VariableNode
                || (right instanceof BinaryNode b && b.operator() == Operator.POWER && b.left() instanceof VariableNode);
    }

    private String wrap(ExpressionNode child, Operator parent, boolean isLeft) {
        String text = child.accept(this);
        int childPrecedence = precedence(child);
        boolean needsParens;
        if (childPrecedence < parent.precedence()) {
            needsParens = true;
        } else if (childPrecedence == parent.precedence() && child instanceof BinaryNode) {
            // Equal precedence: parentheses only where the default associativity would regroup the tree.
            needsParens = parent.isRightAssociative() ? isLeft : !isLeft;
        } else {
            needsParens = false;
        }
        return needsParens ? "(" + text + ")" : text;
    }

    private static int precedence(ExpressionNode node) {
        return switch (node) {
            case BinaryNode b -> b.operator().precedence();
            case UnaryNode u -> u.operator().precedence();
            default -> Integer.MAX_VALUE;
        };
    }
}
