package com.reimbes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByImage(String image);
    List<Transaction> findByReimsUserAndDateBetweenAndCategory(ReimsUser user, long start, long end, Transaction.Category category);
    List<Transaction> findByReimsUser(ReimsUser user);
    List<Transaction> findByReimsUserAndCategory(ReimsUser user, Transaction.Category category);
    Page<Transaction> findByReimsUserAndTitleContainingIgnoreCase(ReimsUser user, String title, Pageable pageable);
    Page<Transaction> findByReimsUserAndTitleContainingIgnoreCaseAndCategory(ReimsUser user, String title,
                                                                             Transaction.Category category, Pageable pageable);
    List<Transaction> findByReimsUserAndDateBetween(ReimsUser user, long start, long end);
    Page<Transaction> findByReimsUserAndTitleContainingIgnoreCaseAndDateBetween(ReimsUser user, String title, long start,
                                                                                long end, Pageable pageable);

    Page<Transaction> findByReimsUserAndTitleContainingIgnoreCaseAndCategoryAndDateBetween(ReimsUser user,
                                                                                           String title,
                                                                                           Transaction.Category category,
                                                                                           long startDate,
                                                                                           long endDate,
                                                                                           Pageable pageable);
}
