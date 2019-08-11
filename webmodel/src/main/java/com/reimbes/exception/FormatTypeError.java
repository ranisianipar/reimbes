package com.reimbes.exception;

import com.reimbes.constant.ResponseCode;
import org.springframework.http.HttpStatus;

public class FormatTypeError extends ReimsException {

    public FormatTypeError(String errorMessage) {
        super("INCOMPATIBLE_FORMAT => "+errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, ResponseCode.INTERNAL_SERVER_ERROR);
    }
}
