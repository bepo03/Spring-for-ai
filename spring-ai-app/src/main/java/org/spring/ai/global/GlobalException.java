package org.spring.ai.global;

import org.spring.ai.global.custom.ContextLengthExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(ContextLengthExceededException.class)
    public ResponseEntity<ApiResponse<?>> contextLengthExceededException(Exception e) {
        return ResponseEntity.status(500)
                .body(ApiResponse.failure(e.getMessage()));
    }
}
