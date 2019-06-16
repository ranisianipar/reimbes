package com.reimbes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuelRepository extends JpaRepository<Fuel, Long> {
    List<Fuel> findByUser(ReimsUser user, Pageable pageable);
    void deleteByUser(ReimsUser user);
    void deleteByIds(List<Long> ids);
}
