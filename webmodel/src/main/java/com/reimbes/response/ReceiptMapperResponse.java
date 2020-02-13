package com.reimbes.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReceiptMapperResponse {
    private long amount;
}
