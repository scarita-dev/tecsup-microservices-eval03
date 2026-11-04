package com.biblioteca.libro.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> details
) {

    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, Map.of());
    }

    public static ErrorResponse of(int status, String error, String message, String path, Map<String, String> details) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, details);
    }
}
