package com.reimbes.implementation;

import com.reimbes.*;
import com.reimbes.interfaces.ParkingService;
import com.reimbes.request.TransactionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import static com.sun.xml.internal.bind.v2.util.EditDistance.editDistance;

@Service
public class ParkingServiceImpl implements ParkingService {

    private static Logger log = LoggerFactory.getLogger(ParkingServiceImpl.class);

    @Autowired
    private UserServiceImpl userService;

    @Override
    public Parking create(TransactionRequest request) {
        Parking transaction = new Parking();
        transaction.setCategory(Transaction.Category.PARKING);
        transaction.setHours(request.getHours());
        transaction.setType(request.getParkingType());
        transaction.setLocation(request.getLocation());
        return transaction;
    }
}
