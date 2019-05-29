package com.reimbes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    public List<Fuel> findByCategory(Fuel.Category category);
    public List<Fuel> findByCategoryAndUser(Fuel.Category category, ReimsUser user);
    public List<Fuel> findByUser(ReimsUser user);
    public void deleteByUser(ReimsUser user);
}
