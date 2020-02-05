package com.reimbes.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ReimsException extends Exception {
    private String message;
    private HttpStatus httpStatus;
    private int code;

    public ReimsException (String errorMessage, HttpStatus httpStatus, int code) {
        this.message = errorMessage;
        this.httpStatus = httpStatus;
        this.code = code;
    }
}
