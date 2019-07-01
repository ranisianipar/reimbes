package com.reimbes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCategory(Fuel.Category category);
    List<Transaction> findByCategoryAndUser(Fuel.Category category, ReimsUser user);
    List<Transaction> findByIds(List<Long> ids);
    List<Transaction> findByUser(ReimsUser user);
    List<Transaction> findByUserAndDateBetweenAndTitleContaining(ReimsUser user, Date startDate, Date endDate, String title, Pageable pageable);
    void deleteByUser(ReimsUser user);
}
