package org.spring.ai.global.custom;

public class ContextLengthExceededException extends RuntimeException {
    public ContextLengthExceededException(String message) {
        super(message);
    }
}
