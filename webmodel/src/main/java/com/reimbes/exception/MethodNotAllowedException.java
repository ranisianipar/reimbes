package com.reimbes.exception;

import com.reimbes.constant.ResponseCode;
import org.springframework.http.HttpStatus;

public class MethodNotAllowedException extends ReimsException {

    public MethodNotAllowedException(String errorMessage) {
        super("NOT ALLOWED => " + errorMessage, HttpStatus.METHOD_NOT_ALLOWED, ResponseCode.METHOD_NOT_ALLOWED);
    }
}
