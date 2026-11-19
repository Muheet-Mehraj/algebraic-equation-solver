package com.equationsolver.equations.api;

import com.equationsolver.equations.service.EquationService;
import com.equationsolver.equations.service.StoredEquation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equations")
@Tag(name = "Equations")
public class EquationController {

    private final EquationService service;

    public EquationController(EquationService service) {
        this.service = service;
    }

    @PostMapping("/store")
    @Operation(summary = "Store an equation",
            description = "Parses the infix equation into postfix form, builds the expression tree and stores it.")
    @ApiResponse(responseCode = "201", description = "Equation stored")
    @ApiResponse(responseCode = "400", description = "Blank, too long or syntactically invalid equation",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<StoreEquationResponse> store(@Valid @RequestBody StoreEquationRequest request) {
        StoredEquation stored = service.store(request.equation());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/equations/{id}").buildAndExpand(stored.id()).toUri();
        return ResponseEntity.created(location)
                .body(new StoreEquationResponse("Equation stored successfully", stored.id(), stored.postfix()));
    }

    @GetMapping
    @Operation(summary = "List stored equations",
            description = "Each equation is reconstructed in infix form by traversing its stored tree.")
    @ApiResponse(responseCode = "200", description = "All stored equations, oldest first")
    public EquationListResponse list() {
        List<EquationSummary> items = service.findAll().stream().map(EquationSummary::from).toList();
        return new EquationListResponse(items);
    }

    @GetMapping("/{equationId}")
    @Operation(summary = "Get one stored equation")
    @ApiResponse(responseCode = "200", description = "The stored equation")
    @ApiResponse(responseCode = "404", description = "No equation with that id",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public EquationSummary get(@PathVariable String equationId) {
        return EquationSummary.from(service.get(equationId));
    }

    @PostMapping("/{equationId}/evaluate")
    @Operation(summary = "Evaluate an equation",
            description = "Substitutes the given variable values into the stored tree and returns the result.")
    @ApiResponse(responseCode = "200", description = "Evaluation result")
    @ApiResponse(responseCode = "400", description = "Missing, null or non-numeric variable values",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "No equation with that id",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422", description = "Not computable for these values (e.g. division by zero)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public EvaluateResponse evaluate(@PathVariable String equationId,
                                     @Valid @RequestBody(required = false) EvaluateRequest request) {
        Map<String, Double> variables = request == null ? Map.of() : request.variablesOrEmpty();
        StoredEquation equation = service.get(equationId);
        double result = service.evaluate(equationId, variables);
        return new EvaluateResponse(equation.id(), equation.infix(), EvaluateResponse.normalize(variables),
                EvaluateResponse.normalize(result));
    }
}