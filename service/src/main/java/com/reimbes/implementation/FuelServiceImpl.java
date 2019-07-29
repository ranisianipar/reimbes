package com.reimbes.implementation;

import com.reimbes.*;
import com.reimbes.request.TransactionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FuelServiceImpl implements FuelService {

    private static Logger log = LoggerFactory.getLogger(FuelServiceImpl.class);

    @Autowired
    private FuelRepository fuelRepository;

    @Override
    public Fuel create(TransactionRequest req) {
        Fuel fuel = new Fuel();
        fuel.setLiters(req.getLiters());
        fuel.setCategory(Transaction.Category.FUEL);
        fuel.setType(req.getFuelType());

        return fuel;
    }
}
