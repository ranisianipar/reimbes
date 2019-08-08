package com.reimbes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByImage(String image);
    List<Transaction> findByIdIn(List<Long> id);
    Transaction findByImageContaining(String image);
    List<Transaction> findByReimsUser(ReimsUser user);
    Page<Transaction> findByReimsUserAndTitleContaining(ReimsUser user, String title, Pageable pageable);
    Page<Transaction> findByReimsUserAndTitleContainingAndCategory(ReimsUser user, String title,
                                                                   Transaction.Category category, Pageable pageable);
    List<Transaction> findByReimsUserAndDateBetween(ReimsUser user, long start, long end);
    Page<Transaction> findByReimsUserAndTitleContainingAndDateBetween(ReimsUser user, String title, long start,
                                                                      long end, Pageable pageable);

    Page<Transaction> findByReimsUserAndTitleContainingAndCategoryAndDateBetween(ReimsUser user,
                                                                                 String title,
                                                                                 Transaction.Category category,
                                                                                 long startDate,
                                                                                 long endDate,
                                                                                 Pageable pageable);
}
