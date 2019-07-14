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
    List<Transaction> findByCategoryAndReimsUser(Transaction.Category category, ReimsUser user);
    List<Transaction> findByIdIn(List<Long> id);
    List<Transaction> findByReimsUser(ReimsUser user);
    Page<Transaction> findByReimsUser(ReimsUser user, Pageable pageable);
    List<Transaction> findByReimsUserAndDateBetweenAndTitleContaining(ReimsUser user, Date startDate, Date endDate, String title, Pageable pageable);
    List<Transaction> findByReimsUserAndDateBetween(ReimsUser user, Date start, Date end);
    void deleteByReimsUser(ReimsUser user);
}
