package com.reimbes.response;

import com.reimbes.constant.ResponseCode;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

@Data
public class BaseResponse<T> {
    private int code;

    private HttpStatus status;
    private String errors;
    Pageable paging;
    long totalRecords;
    T data;
    boolean success;

    public BaseResponse() {
        this.code = ResponseCode.OK;
        this.success = true;
    }

}
