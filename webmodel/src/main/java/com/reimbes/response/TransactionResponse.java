package com.reimbes.response;

import com.reimbes.Transaction;
import com.reimbes.request.TransactionRequest;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionResponse<T extends TransactionRequest> {
    private long id;
    private String imagePath;
    private String user; // contains the username

    private Date createdDate;
    private Date updatedDate;

    // OCR results
    private Date date;
    private long amount;
    private Transaction.Category category;

    private T transactionDetails;


}
