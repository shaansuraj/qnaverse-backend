package com.qnaverse.QnAverse.exceptions;

/**
 * Exception thrown when a user attempts an unauthorized action.
 */
public class UnauthorizedException extends CustomException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
