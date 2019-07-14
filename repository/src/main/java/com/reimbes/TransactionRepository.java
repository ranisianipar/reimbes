package com.reimbes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByImage(String image);
    List<Transaction> findByCategory(Transaction.Category category);
    List<Transaction> findByCategoryAndUser(Transaction.Category category, ReimsUser user);
    List<Transaction> findByIdIn(List<Long> id);
    List<Transaction> findByUser(ReimsUser user);
    Page<Transaction> findByUser(ReimsUser user, Pageable pageable);
    List<Transaction> findByUserAndDateBetween(ReimsUser user, Date start, Date end);
    List<Transaction> findByUserAndDateBetweenAndTitleContaining(ReimsUser user, Date startDate, Date endDate, String title, Pageable pageable);
    void deleteByUser(ReimsUser user);
}
