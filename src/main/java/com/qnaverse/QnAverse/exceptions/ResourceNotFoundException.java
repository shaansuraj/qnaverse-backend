package com.qnaverse.QnAverse.exceptions;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends CustomException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
