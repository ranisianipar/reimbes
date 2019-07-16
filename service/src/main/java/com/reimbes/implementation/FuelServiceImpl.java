package com.reimbes.implementation;

import com.reimbes.*;
import com.reimbes.request.TransactionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuelServiceImpl implements FuelService {

    private static Logger log = LoggerFactory.getLogger(FuelServiceImpl.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FuelRepository fuelRepository;

    @Override
    public Fuel get(String id) {
        return null;
    }

    @Override
    public List<Fuel> getByUser(ReimsUser user) {

        return fuelRepository.findByUser(user);
    }

    @Override
    public void delete(long id) {
        fuelRepository.delete(id);
    }

    public Fuel create(TransactionRequest transaction) {
        Fuel fuel = new Fuel();
        fuel.setLiters(transaction.getLiters());
        fuel.setCategory(Transaction.Category.FUEL);

        return fuel;
    }
}
