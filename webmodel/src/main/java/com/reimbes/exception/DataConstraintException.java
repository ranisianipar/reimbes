package com.reimbes.exception;

import org.springframework.http.HttpStatus;

public class DataConstraintException extends ReimsException {

    public DataConstraintException(String errorMessage) {
        super("Data Constraint => "+errorMessage, HttpStatus.BAD_REQUEST);
    }
}
