package com.reimbes;

import com.reimbes.exception.ReimsException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface TransactionService {
    Transaction create(HttpServletRequest req, Transaction transaction) throws ReimsException;
    void delete(HttpServletRequest req, long id) throws ReimsException;
    void deleteAll(HttpServletRequest req) throws ReimsException;
    Transaction get(HttpServletRequest req, long id) throws ReimsException;
    List<Transaction> getAll(HttpServletRequest request, Pageable pageable);
    String upload(HttpServletRequest req, MultipartFile image) throws Exception;
    byte[] getPhoto(String imagePath); // is it ok to dont check the user?
    void deletePhoto(String imagePath);

    // .xls
}
