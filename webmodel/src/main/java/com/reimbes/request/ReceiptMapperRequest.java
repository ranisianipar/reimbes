package com.reimbes.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReceiptMapperRequest {
    private String image;

    public ReceiptMapperRequest(String image) {
        this.image = image;
    }
}
