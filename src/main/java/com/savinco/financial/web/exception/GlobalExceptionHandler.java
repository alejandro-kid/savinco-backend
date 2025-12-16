package com.savinco.financial.web.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        StringBuilder messageBuilder = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            if (errorMessage != null && !errorMessage.isBlank()) {
                messageBuilder.append(errorMessage).append("; ");
            }
        });
        
        String message = messageBuilder.toString();
        if (message.endsWith("; ")) {
            message = message.substring(0, message.length() - 2);
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(message)
            .errors(errors)
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        // Check if it's a "not found" error (404) - some services use IllegalArgumentException for not found
        String message = ex.getMessage().toLowerCase();
        HttpStatus status = (message.contains("not found") || message.contains("does not exist")) 
            ? HttpStatus.NOT_FOUND 
            : HttpStatus.BAD_REQUEST;
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .status(status.value())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        // Check if it's a "not found" error (404) or a conflict error (409)
        String message = ex.getMessage().toLowerCase();
        HttpStatus status;
        if (message.contains("not found") || message.contains("does not exist")) {
            status = HttpStatus.NOT_FOUND;
        } else if (message.contains("base currency already exists") || message.contains("cannot update exchange rate for base currency")) {
            // Business rule: attempting to create a second base currency or update base currency rate is a bad request
            status = HttpStatus.BAD_REQUEST;
        } else {
            status = HttpStatus.CONFLICT;
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .status(status.value())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private int status;
        private String message;
        private Map<String, String> errors;
        private LocalDateTime timestamp;
    }
}
