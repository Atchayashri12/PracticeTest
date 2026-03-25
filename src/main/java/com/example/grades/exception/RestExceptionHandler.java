package com.example.grades.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {

    public record ApiError(Instant timestamp, int status, String error, String path, List<String> messages) {}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(fe -> fe.getField() + " " + fe.getDefaultMessage()).toList();
        return ResponseEntity.badRequest()
                .body(new ApiError(Instant.now(), 400, "Validation Error", req.getRequestURI(), errors));
        // 400 instead of 500
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        List<String> errors = ex.getConstraintViolations().stream().map(v -> v.getMessage()).toList();
        return ResponseEntity.badRequest()
                .body(new ApiError(Instant.now(), 400, "Constraint Violation", req.getRequestURI(), errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        // e.g., Duplicate entry 'x@y.com' for key 'uk_email'
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(Instant.now(), 409, "Data Integrity Violation", req.getRequestURI(), List.of(msg)));
        // 409 instead of 500
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(500)
                .body(new ApiError(Instant.now(), 500, "Server Error", req.getRequestURI(), List.of(ex.getMessage())));
    }
}
