package com.example.trust.common;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDb(DataAccessException e) {
        return ResponseEntity.status(500).body(Map.of(
                "error", "DB_ERROR",
                "message", e.getMostSpecificCause().getMessage()
        ));
    }
}
