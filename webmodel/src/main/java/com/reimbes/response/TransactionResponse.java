package com.reimbes.response;

import com.reimbes.Transaction;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionResponse<T> {
    private long id;

    private String image;

    private Date created_at;

    private Transaction.Category category;

    private T transactionDetails;


}
