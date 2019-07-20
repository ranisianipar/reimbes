package com.reimbes.response;

import com.reimbes.Fuel;
import lombok.Data;

import java.util.Date;

@Data
public class FuelResponse extends TransactionResponse {
    private int liters;
    private Fuel.Type type;
}
