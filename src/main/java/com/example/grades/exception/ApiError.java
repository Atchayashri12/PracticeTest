
package com.example.grades.exception;

import java.time.Instant;
import java.util.List;

public record ApiError(Instant timestamp, int status, String error, String path, List<String> messages) {}