package edu.uic.marketplace.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(Map.of(
                        "status", e.getStatusCode(),
                        "message", e.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(RuntimeException e) {
        // Return 400 with domain-specific error message
        return ResponseEntity
                .status(400)
                .body(Map.of(
                        "status", 400,
                        "message", e.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        // Generic fallback for unexpected errors
        return ResponseEntity
                .status(500)
                .body(Map.of(
                        "status", 500,
                        "message", "An internal error occurred.",
                        "timestamp", LocalDateTime.now()
                ));
    }
}
