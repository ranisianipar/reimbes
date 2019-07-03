package com.reimbes.request;

import com.reimbes.Transaction;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionRequest {
    private long id;

    private String title;

    private long amount;
    private Transaction.Category category;

    private Date date;
    private String image;

    // standard crud attributes
    private long createdAt;


    // all uncovered attributes of specific transaction
    private int liters;
    private int hours;

}
