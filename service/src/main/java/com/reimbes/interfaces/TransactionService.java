package com.reimbes.interfaces;

import com.reimbes.ReimsUser;
import com.reimbes.Transaction;
import com.reimbes.exception.ReimsException;
import com.reimbes.request.TransactionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {
    List<Transaction>getByDateAndType(Long start, Long end, Transaction.Category category) throws ReimsException;
    Transaction createByImage(String transaction) throws ReimsException;
    Transaction update(Transaction transaction) throws ReimsException;
    void delete(long id) throws ReimsException;
    void deleteByUser(ReimsUser user);
    Transaction get(long id) throws ReimsException;
    Page<Transaction> getAll(Pageable pageable, String startDate, String endDate, String searchTitle,
                             Transaction.Category category) throws ReimsException;

    List<Transaction> getByUser(ReimsUser user);
    List<Transaction> getByUserAndDate(ReimsUser user, long start, long end);
}
