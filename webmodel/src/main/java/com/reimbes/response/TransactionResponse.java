package com.reimbes.response;

import com.reimbes.Transaction;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionResponse {
    private long id;
    private String imagePath;
    private String user; // contains the username

    private Date createdDate;
    private Date updatedDate;

    // OCR results
    private Date date;
    private long amount;
    private Transaction.Category category;

    // some specific attributes those depending to the category
    // ....


}
