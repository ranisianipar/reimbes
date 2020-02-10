package com.reimbes.response;

import com.reimbes.Transaction;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TransactionResponse {
    private long id;

    private Transaction.Category category;

    private long userId;

    private long date;

    private long amount;

    private List<String> attachments;

    private String title;

    private String location;


}
