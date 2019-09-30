package com.reimbes;

import com.reimbes.exception.ReimsException;
import com.reimbes.request.TransactionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {
    Transaction createByImage(String transaction) throws ReimsException;
    String uploadImage(byte[] image, String extension) throws Exception;
    Transaction update(TransactionRequest transaction) throws ReimsException;
    void delete(long id) throws ReimsException;
    void deleteByUser(ReimsUser user);
    Transaction get(long id) throws ReimsException;
    Page<Transaction> getAll(Pageable pageable, String startDate, String endDate, String searchTitle,
                             Transaction.Category category) throws ReimsException;

    List<Transaction> getByUser(ReimsUser user);
    List<Transaction> getByUserAndDate(ReimsUser user, long start, long end);
    String getImage(long id, String imageName) throws ReimsException; // is it ok to dont check the medicalUser?
    // .xls
}
