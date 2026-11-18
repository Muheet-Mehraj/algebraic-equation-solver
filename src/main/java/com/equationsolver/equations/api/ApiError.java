package com.equationsolver.equations.api;

import java.time.Instant;
import java.util.List;

/** Uniform error body returned for every failure. */
public record ApiError(Instant timestamp, int status, String error, String message, String path, List<String> details) {
}
