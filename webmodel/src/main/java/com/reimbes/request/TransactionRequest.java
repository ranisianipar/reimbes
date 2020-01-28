package com.reimbes.request;

import com.reimbes.Fuel;
import com.reimbes.Parking;
import com.reimbes.Transaction;
import lombok.Data;

@Data
public class TransactionRequest {
    private long id;

    private String title;

    private long amount;
    private Transaction.Category category;

    private long date;
    private String image;

    // standard crud attributes
    private long createdAt;


    // all uncovered attributes of specific transaction

    /* FUEL */
    private float liters;
    private Fuel.Type fuelType;


    /* PARKING */
    private int hours;
    private Parking.Type parkingType;
    private String location;

}
