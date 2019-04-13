package com.reimbes.exception;

import com.reimbes.constant.ResponseCode;
import org.springframework.http.HttpStatus;

public class DataConstraintException extends ReimsException {

    public DataConstraintException(String errorMessage) {
        super("Data Constraint => "+errorMessage, HttpStatus.BAD_REQUEST, ResponseCode.CODE_BAD_REQUEST);
    }
}
