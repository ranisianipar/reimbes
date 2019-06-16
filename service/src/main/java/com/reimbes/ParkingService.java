package com.reimbes;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ParkingService {
    Parking create(Parking parking);
    Parking get(long id);
    List<Parking> getAll(Pageable pageable);
    void delete(long id);
    void deleteMany(List<Long> ids);
    void deleteByUser(ReimsUser user);
}
