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
    List<Transaction> findByIdIn(List<Long> id);
    List<Transaction> findByReimsUser(ReimsUser user);
    Page<Transaction> findByReimsUser(ReimsUser user, Pageable pageable);
    Page<Transaction> findByReimsUserAndCategory(ReimsUser user, Transaction.Category category, Pageable pageable);
    Page<Transaction> findByReimsUserAndCategoryAndTitleContaining(ReimsUser user, Transaction.Category category,
                                                                   String title, Pageable pageable);
    List<Transaction> findByReimsUserAndDateBetween(ReimsUser user, Date start, Date end);
    Page<Transaction> findByReimsUserAndDateBetweenAndTitleContaining(ReimsUser user,
                                                                      Date startDate,
                                                                      Date endDate,
                                                                      String title,
                                                                      Pageable pageable);
    Page<Transaction> findByReimsUserAndDateBetweenAndTitleContainingAndCategory(ReimsUser user,
                                                                                 Date startDate,
                                                                                 Date endDate,
                                                                                 String title,
                                                                                 Transaction.Category category,
                                                                                 Pageable pageable);
    void deleteByReimsUser(ReimsUser user);
}
