package com.equationsolver.equations.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record EvaluateRequest(
        @Schema(example = "{\"x\": 2, \"y\": 3, \"z\": 1}")
        Map<String, @NotNull(message = "variable values must not be null") Double> variables) {

    public Map<String, Double> variablesOrEmpty() {
        return variables == null ? Map.of() : variables;
    }
}
