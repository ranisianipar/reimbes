package com.reimbes.response;

import com.reimbes.Transaction;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionResponse {
    private long id;

    private Transaction.Category category;

    private long userId;

    private String date;

    private long amount;

    private String image;

    private String title;


}
