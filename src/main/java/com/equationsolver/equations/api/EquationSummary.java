package com.equationsolver.equations.api;

import com.equationsolver.equations.service.StoredEquation;
import io.swagger.v3.oas.annotations.media.Schema;

public record EquationSummary(
        @Schema(example = "1") String equationId,
        @Schema(example = "3x + 2y - z", description = "Infix text reconstructed from the stored tree") String equation,
        @Schema(example = "3 x * 2 y * + z -") String postfix) {

    public static EquationSummary from(StoredEquation e) {
        return new EquationSummary(e.id(), e.infix(), e.postfix());
    }
}
