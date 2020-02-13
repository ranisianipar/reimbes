package com.reimbes.request;

import com.reimbes.Fuel;
import com.reimbes.Parking;
import com.reimbes.Transaction;
import lombok.Data;

import java.util.List;

@Data
public class TransactionRequest {
    private long id;

    private String title;

    private long amount;
    private Transaction.Category category;

    private long date;
    private List<String> attachments;

    // standard crud attributes
    private long createdAt;
    private String location;


    // all uncovered attributes of specific transaction

    /* FUEL_VALUE */
    private float liters;
    private long kilometers;
    private Fuel.Type fuelType;


}
