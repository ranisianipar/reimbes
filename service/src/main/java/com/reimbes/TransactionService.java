package com.reimbes;

public interface TransactionService {
    public Transaction create(Transaction transaction);
    public Transaction update(Transaction transaction);
    public void delete(long id);
    public void deleteAll();
    public Transaction get(long id);

    // .xls
}
