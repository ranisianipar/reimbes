package com.reimbes.request;

import com.reimbes.Transaction;
import lombok.Data;

@Data
public class TransactionRequest {
    private long amount;
    private Transaction.Category category;
}
