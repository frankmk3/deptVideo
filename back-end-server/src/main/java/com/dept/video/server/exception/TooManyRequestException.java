package com.dept.video.server.exception;

public class TooManyRequestException extends Exception {

    private static final long serialVersionUID = 1L;

    public TooManyRequestException(String message) {
        super(message);
    }

    public TooManyRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}