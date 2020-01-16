package com.reimbes.implementation;

import com.reimbes.*;
import com.reimbes.interfaces.FuelService;
import com.reimbes.request.TransactionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FuelServiceImpl implements FuelService {

    private static Logger log = LoggerFactory.getLogger(FuelServiceImpl.class);

    @Override
    public Fuel create(TransactionRequest req) {
        Fuel fuel = new Fuel();
        fuel.setLiters(req.getLiters());
        fuel.setCategory(Transaction.Category.FUEL);
        fuel.setType(req.getFuelType());

        return fuel;
    }

    @Override
    public Fuel map(String[] source) {
        Fuel fuel = new Fuel();
        fuel.setCategory(Transaction.Category.FUEL);

        try {
            fuel.setAmount(Integer.parseInt(source[13].replaceAll("[^\\d]","")));
            ((Fuel) fuel).setLiters(Integer.parseInt(source[12].replaceAll("[^\\d]","")));

        }   catch (Exception e) {
            log.info(e.getMessage());
        }

        return fuel;
    }
}
