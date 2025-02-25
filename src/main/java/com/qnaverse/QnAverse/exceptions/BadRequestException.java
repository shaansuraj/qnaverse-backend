package com.qnaverse.QnAverse.exceptions;

/**
 * Exception thrown for bad user requests.
 */
public class BadRequestException extends CustomException {

    public BadRequestException(String message) {
        super(message);
    }
}
