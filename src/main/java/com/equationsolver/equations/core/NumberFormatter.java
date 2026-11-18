package com.equationsolver.equations.core;

import java.math.BigDecimal;

/** Shared number rendering: 3.50 -> "3.5", 4.0 -> "4". */
final class NumberFormatter {
    private NumberFormatter() {
    }

    static String format(BigDecimal value) {
        BigDecimal stripped = value.stripTrailingZeros();
        return stripped.scale() < 0 ? stripped.setScale(0).toPlainString() : stripped.toPlainString();
    }
}
