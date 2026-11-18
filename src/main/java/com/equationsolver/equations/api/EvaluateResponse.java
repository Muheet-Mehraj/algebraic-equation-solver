package com.equationsolver.equations.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.LinkedHashMap;
import java.util.Map;

public record EvaluateResponse(
        String equationId,
        String equation,
        Map<String, Number> variables,
        @Schema(example = "10", description = "Whole numbers are returned without a decimal part") Number result) {

    /** Echoes the supplied values the way they were written (2, not 2.0). */
    public static Map<String, Number> normalize(Map<String, Double> values) {
        Map<String, Number> out = new LinkedHashMap<>();
        values.forEach((name, value) -> out.put(name, normalize(value)));
        return out;
    }

    /** Returns 10 instead of 10.0 for whole-number results. */
    public static Number normalize(double value) {
        if (value == Math.rint(value) && Math.abs(value) < 1e15) {
            return (long) value;
        }
        return value;
    }
}
