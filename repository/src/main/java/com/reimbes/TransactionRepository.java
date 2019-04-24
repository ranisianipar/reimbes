package com.reimbes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    public List<Transaction> findByCategory(Transaction.Category category);
    public List<Transaction> findByCategoryAndUser(Transaction.Category category, ReimsUser user);
    public List<Transaction> findByUser(ReimsUser user);
    public void deleteByUser(ReimsUser user);
}
