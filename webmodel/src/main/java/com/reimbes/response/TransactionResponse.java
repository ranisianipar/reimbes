package com.reimbes.response;

import com.reimbes.Transaction;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionResponse {
    private long id;

    private Transaction.Category category;

    private long user;

    private Date date;

    private long amount;

    private String image;


}
