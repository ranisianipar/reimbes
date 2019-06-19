package com.reimbes;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FuelService {
    Fuel get(String id);
    List<Fuel> getByUser(ReimsUser user, Pageable page);
    void delete(List<Long> id);
    Fuel create(Transaction transaction);
    Fuel update(long id, Fuel newData);
}
