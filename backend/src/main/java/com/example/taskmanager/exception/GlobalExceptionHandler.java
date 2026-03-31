package com.example.taskmanager.exception;

import com.example.taskmanager.entity.TaskStatus;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("detail", ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("dueDate", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            body.put(field, error.getDefaultMessage());
        });

        if (body.isEmpty()) {
            body.put("detail", "Validation failed");
        }

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            String field = extractLastFieldName(invalidFormatException);
            Class<?> targetType = invalidFormatException.getTargetType();

            if (field != null && targetType != null) {
                if (TaskStatus.class.equals(targetType)) {
                    return ResponseEntity.badRequest().body(Map.of(field, "Invalid status"));
                }
                if (Instant.class.equals(targetType)) {
                    return ResponseEntity.badRequest().body(Map.of(field, "Invalid due date/time"));
                }
            }
        }

        return ResponseEntity.badRequest().body(Map.of("detail", "Malformed JSON request"));
    }

    private String extractLastFieldName(JsonMappingException ex) {
        if (ex.getPath() == null || ex.getPath().isEmpty()) {
            return null;
        }
        JsonMappingException.Reference last = ex.getPath().get(ex.getPath().size() - 1);
        return last.getFieldName();
    }
}
