package com.reimbes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, Long> {
    List<Parking> findByUser(ReimsUser user, Pageable pageable);
    void deleteByUser(ReimsUser user);
    void deleteByIds(List<Long> ids);
}
