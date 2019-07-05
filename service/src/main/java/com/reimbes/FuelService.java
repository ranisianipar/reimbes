package com.reimbes;

import com.reimbes.request.TransactionRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FuelService {
    Fuel get(String id);
    List<Fuel> getByUser(ReimsUser user, Pageable page);
    void delete(long id);
    //Fuel create(Fuel transaction);
    //Fuel update(TransactionRequest newData, boolean categoryChange);
}
