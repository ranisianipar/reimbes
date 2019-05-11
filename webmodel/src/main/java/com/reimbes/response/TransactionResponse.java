package com.reimbes.response;

import com.reimbes.Transaction;
import lombok.Data;

@Data
public class TransactionResponse<T extends Transaction> {
    private long id;

    private Transaction.Category category;

    private T details;


}
