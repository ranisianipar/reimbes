package com.reimbes.request;

import com.reimbes.Transaction;
import lombok.Data;

public abstract class TransactionRequest {
    private long amount;
    private Transaction.Category category;
}
