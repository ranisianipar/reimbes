package com.reimbes.request;

import lombok.Data;

@Data
public class FuelRequest extends TransactionRequest {
    private int liters;

}
