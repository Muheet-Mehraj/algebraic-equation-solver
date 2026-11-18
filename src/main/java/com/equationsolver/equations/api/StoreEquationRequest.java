package com.equationsolver.equations.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StoreEquationRequest(
        @Schema(example = "3x + 2y - z", description = "Infix equation. Supports + - * / ^, parentheses, unary minus, implicit multiplication (3x, 2(x+1)).")
        @NotBlank(message = "equation must not be blank")
        @Size(max = 500, message = "equation must be at most 500 characters")
        String equation) {
}
