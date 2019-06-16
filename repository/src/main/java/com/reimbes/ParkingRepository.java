package com.reimbes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, Long> {
    public List<Fuel> findByUser(ReimsUser user, Pageable pageable);
    public void deleteByUser(ReimsUser user);
}
