package com.reimbes.response;

import com.reimbes.constant.ResponseCode;
import com.reimbes.exception.ReimsException;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

@Data
public class BaseResponse<T> {
    private int code;

    private HttpStatus status;
    private String errors;
    private Paging paging;
    private T data;
    private boolean success;

    public BaseResponse() {
        this.code = ResponseCode.OK;
        this.success = true;
        this.status = HttpStatus.OK;
    }

    public void setErrorResponse(ReimsException r) {
        this.code = r.getCode();
        this.status = r.getHttpStatus();
        this.errors = r.getMessage();
        this.success = false;
    }
}
