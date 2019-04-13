package com.reimbes.exception;

import com.reimbes.response.BaseResponse;
import org.springframework.http.HttpStatus;

public class ReimsException extends Exception {
    private String message;
    private HttpStatus httpStatus;
    private int code;

    public ReimsException (String errorMessage, HttpStatus httpStatus, int code) {
        this.message = errorMessage;
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public static BaseResponse getErrorResponse(ReimsException r) {
        BaseResponse br = new BaseResponse();
        br.setErrorMessage(r.getMessage());
        br.setSuccess(false);
        br.setCode(r.code);
        br.setErrorCode(r.httpStatus);
        return br;
    }
}
