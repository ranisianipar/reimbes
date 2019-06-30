package com.reimbes.response;

import lombok.Data;

import java.util.Date;

@Data
public class FuelResponse extends TransactionResponse {
    private int liters;
}
