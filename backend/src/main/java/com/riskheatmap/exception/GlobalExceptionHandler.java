package com.riskheatmap.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        log.warn("Not found: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND,
                ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(err -> {
            String field = ((FieldError) err).getField();
            fieldErrors.put(field, err.getDefaultMessage());
        });
        Map<String, Object> body = buildBody(
                HttpStatus.BAD_REQUEST, "Validation failed", request);
        body.put("fieldErrors", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(
            Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred", request);
    }

    private ResponseEntity<Map<String, Object>> buildError(
            HttpStatus status,
            String message,
            WebRequest request) {
        return ResponseEntity.status(status.value()).body(
        buildBody(status, message, request));
    
    
    }

    private Map<String, Object> buildBody(
            HttpStatus status,
            String message,
            WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false)
                .replace("uri=", ""));
        return body;
    }
}