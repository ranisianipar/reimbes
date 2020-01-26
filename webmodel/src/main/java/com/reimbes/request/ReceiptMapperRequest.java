package com.reimbes.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReceiptMapperRequest {
    private String requestId;
    private String image;

    public ReceiptMapperRequest(String requestId, String image) {
        this.requestId = requestId;
        this.image = image;
    }
}
