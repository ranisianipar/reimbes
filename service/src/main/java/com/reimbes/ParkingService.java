package com.reimbes;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ParkingService {
    Parking get(String id);
    List<Parking> getByUser(ReimsUser user, Pageable page);
    void delete(List<Long> id);
    Parking create(Transaction transaction);
    Parking update(long id, Parking newData);
}
