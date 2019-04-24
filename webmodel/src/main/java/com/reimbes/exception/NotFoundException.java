package com.reimbes.exception;

import com.reimbes.constant.ResponseCode;
import org.springframework.http.HttpStatus;

public class NotFoundException extends ReimsException {

    public NotFoundException(String errorMessage) {
        super("Not Found => "+errorMessage, HttpStatus.NOT_FOUND, ResponseCode.NOT_FOUND);
    }
}
