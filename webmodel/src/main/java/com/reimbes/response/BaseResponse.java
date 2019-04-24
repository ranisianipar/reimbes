package com.reimbes.response;

import com.reimbes.constant.ResponseCode;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

@Data
public class BaseResponse<T> {
    private int code;

    private HttpStatus errorCode;
    private String errorMessage;
    Pageable paging;
    long totalRecords;
    long totalPages;
    T value;
    boolean success;

    public BaseResponse() {
        this.code = ResponseCode.OK;
        this.success = true;
    }

}
