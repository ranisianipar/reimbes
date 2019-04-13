package com.reimbes;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface TransactionService {
    Transaction create(HttpServletRequest req, Transaction transaction) throws Exception;
    void delete(long id);
    void deleteAll();
    Transaction get(long id);
    String upload(HttpServletRequest req, MultipartFile image) throws Exception;
    byte[] getPhoto(String imagePath);

    // .xls
}
