package com.reimbes.response;

import com.reimbes.Fuel;
import lombok.Data;

import java.util.Date;

@Data
public class FuelResponse extends TransactionResponse {
    private float liters;
    private Fuel.Type type;
}
