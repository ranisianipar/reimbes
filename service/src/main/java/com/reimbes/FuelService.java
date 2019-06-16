package com.reimbes;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FuelService {
    Fuel create(Fuel fuel);
    Fuel get(long id);
    List<Fuel> getAll(Pageable pageable);
    void delete(long id);
    void deleteMany(List<Long> ids);
    void deleteByUser(ReimsUser user);
}
