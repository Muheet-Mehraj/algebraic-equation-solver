package com.equationsolver.equations.api;

import com.equationsolver.equations.core.EquationEvaluationException;
import com.equationsolver.equations.core.InvalidEquationException;
import com.equationsolver.equations.core.MissingVariableException;
import com.equationsolver.equations.service.EquationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

/** Maps domain and validation failures to a consistent {@link ApiError} body. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidEquationException.class)
    public ResponseEntity<ApiError> invalidEquation(InvalidEquationException e, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), request, List.of());
    }

    @ExceptionHandler(MissingVariableException.class)
    public ResponseEntity<ApiError> missingVariables(MissingVariableException e, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), request, e.getMissing());
    }

    @ExceptionHandler(EquationEvaluationException.class)
    public ResponseEntity<ApiError> notComputable(EquationEvaluationException e, HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), request, List.of());
    }

    @ExceptionHandler(EquationNotFoundException.class)
    public ResponseEntity<ApiError> notFound(EquationNotFoundException e, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, e.getMessage(), request, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<String> details = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList();
        String message = details.isEmpty() ? "Validation failed" : details.get(0);
        return build(HttpStatus.BAD_REQUEST, message, request, details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> unreadable(HttpMessageNotReadableException e, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "Request body is missing, is not valid JSON, or contains values of the wrong type", request, List.of());
    }

    private static ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest request,
                                                  List<String> details) {
        ApiError body = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message,
                request.getRequestURI(), details);
        return ResponseEntity.status(status).body(body);
    }
}
