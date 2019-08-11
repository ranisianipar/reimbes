package com.reimbes.exception;

import com.reimbes.constant.ResponseCode;
import org.springframework.http.HttpStatus;

public class NotFoundException extends ReimsException {

    public NotFoundException(String errorMessage) {
        super("NOT_FOUND => "+errorMessage, HttpStatus.NOT_FOUND, ResponseCode.NOT_FOUND);
    }
}
