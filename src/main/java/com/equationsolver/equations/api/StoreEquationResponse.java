package com.equationsolver.equations.api;

import io.swagger.v3.oas.annotations.media.Schema;

public record StoreEquationResponse(
        String message,
        @Schema(example = "1") String equationId,
        @Schema(example = "3 x * 2 y * + z -", description = "Postfix form the tree was built from") String postfix) {
}
