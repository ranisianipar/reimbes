package com.reimbes.interfaces;

import com.reimbes.ReimsUser;
import com.reimbes.Transaction;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import com.reimbes.request.TransactionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {
    List<Transaction> getByDateAndCategory(Long start, Long end, Transaction.Category category) throws ReimsException;
    Transaction createByImageAndCategory(Transaction transaction) throws ReimsException;
    Transaction update(Transaction transaction) throws ReimsException;
    boolean delete(long id) throws NotFoundException;
    boolean deleteTransactionImageByUser(ReimsUser user);
    Transaction get(long id) throws ReimsException;
    Page<Transaction> getAll(Pageable pageable, String startDate, String endDate, String searchTitle,
                             Transaction.Category category) throws ReimsException;

    List<Transaction> getByUser(ReimsUser user);
    List<Transaction> getByUserAndDate(ReimsUser user, long start, long end);
}
