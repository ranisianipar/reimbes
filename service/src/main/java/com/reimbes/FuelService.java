package com.reimbes;

import java.util.List;

public interface FuelService {
    Fuel get(String id);
    List<Fuel> getByUser(ReimsUser user);
    void delete(long id);
    //Fuel create(Fuel transaction);
    //Fuel update(TransactionRequest newData, boolean categoryChange);
}
