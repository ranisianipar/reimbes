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
    Page<Transaction> findByCategoryAndUser(Transaction.Category category, ReimsUser user, Pageable pageable);
    Page<Transaction> findByCategoryAndUserAndTitleContaining(Transaction.Category category,
                                                              ReimsUser user,
                                                              String title, Pageable pageable);
    List<Transaction> findByIdIn(List<Long> id);
    List<Transaction> findByUser(ReimsUser user);
    Page<Transaction> findByUser(ReimsUser user, Pageable pageable);
    List<Transaction> findByUserAndDateBetween(ReimsUser user, Date start, Date end);
    Page<Transaction> findByUserAndDateBetweenAndTitleContaining(ReimsUser user,
                                                                 Date startDate,
                                                                 Date endDate,
                                                                 String title,
                                                                 Pageable pageable);
    Page<Transaction> findByUserAndDateBetweenAndTitleContainingAndCategory(ReimsUser user,
                                                                            Date startDate,
                                                                            Date endDate,
                                                                            String title,
                                                                            Transaction.Category category,
                                                                            Pageable pageable);
    void deleteByUser(ReimsUser user);
}
