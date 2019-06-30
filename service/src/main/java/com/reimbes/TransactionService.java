package com.reimbes;

import com.reimbes.exception.ReimsException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

public interface TransactionService {
    Transaction create(Transaction transaction) throws ReimsException;

    // tagu return type sama parameter
    Transaction update(Transaction transaction, long id);
    void delete(long id) throws ReimsException;
    void deleteAll(HttpServletRequest req) throws ReimsException;
    Transaction get(long id) throws ReimsException;
    List<Transaction> getAll(Pageable pageable, Date startDate, Date endDate, String searchTitle);
    byte[] getPhoto(String imagePath); // is it ok to dont check the user?
    void deletePhoto(String imagePath);

    // .xls
}
