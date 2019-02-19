package com.reimbes.implementation;

import com.reimbes.Transaction;
import com.reimbes.TransactionRepository;
import com.reimbes.TransactionService;
import com.reimbes.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserService userService;

    @Override
    public Transaction create(Transaction transaction) {
        // do validation
        if (transaction.getUser() == null) {
            transaction.setUser(userService.getRandomUser());
        }
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction update(Transaction updatedTransaction) {

        return transactionRepository.save(updatedTransaction);
    }

    @Override
    public void delete(long id) {
        transactionRepository.delete(id);
    }

    @Override
    public void deleteAll() {
        transactionRepository.deleteAll();
    }

    @Override
    public Transaction get(long id) {
        return null;
    }
}
