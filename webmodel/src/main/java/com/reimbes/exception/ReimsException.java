package com.reimbes.exception;

import org.springframework.http.HttpStatus;

public class ReimsException extends Exception {
    private String message;
    private HttpStatus code;

    public ReimsException (String errorMessage, HttpStatus code) {
        this.message = errorMessage;
        this.code = code;
    }
}
