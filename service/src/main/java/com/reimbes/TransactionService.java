package com.reimbes;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface TransactionService {
    Transaction create(HttpServletRequest req, Transaction transaction) throws Exception;
    void delete(long id);
    void deleteAll();
    Transaction get(long id);
    String upload(HttpServletRequest req, MultipartFile image) throws Exception;

    Transaction uploadReal(MultipartFile image);

    // .xls
}
