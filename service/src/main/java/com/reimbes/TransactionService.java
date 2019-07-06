package com.reimbes;

import com.reimbes.exception.ReimsException;
import com.reimbes.request.TransactionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

public interface TransactionService {
    Transaction createByImage(String transaction) throws ReimsException;
    Transaction update(TransactionRequest transaction) throws ReimsException;
    void delete(long id) throws ReimsException;
    Transaction get(long id) throws ReimsException;
    Page<Transaction> getAll(Pageable pageable, Date startDate, Date endDate, String searchTitle) throws ReimsException;
    byte[] getPhoto(String imagePath); // is it ok to dont check the user?
    void deletePhoto(String imagePath);

    // .xls
}
