package com.reimbes;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface TransactionService {
    public Transaction create(Transaction transaction);
    public void delete(long id);
    public void deleteAll();
    public Transaction get(long id);
    public String upload(MultipartFile image) throws IOException;

    // .xls
}
