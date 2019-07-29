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
public class ParkingServiceImpl implements ParkingService {

    private static Logger log = LoggerFactory.getLogger(ParkingServiceImpl.class);

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public Parking create(TransactionRequest request) {
        Parking transaction = new Parking();
        transaction.setCategory(Transaction.Category.PARKING);
        transaction.setHours(request.getHours());
        transaction.setType(request.getParkingType());
        transaction.setLicense(request.getLicense());
        transaction.setLocation(request.getLocation());
        return transaction;
    }

    // map it include the specific attribute
    public Parking mappingToParking(String data) {
        return null;
    }

}
