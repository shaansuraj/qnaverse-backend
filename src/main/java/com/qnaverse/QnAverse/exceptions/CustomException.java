package com.qnaverse.QnAverse.exceptions;

/**
 * Base class for all custom exceptions in the application.
 */
public class CustomException extends RuntimeException {

    public CustomException(String message) {
        super(message);
    }
}
