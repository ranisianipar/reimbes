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
    private AuthServiceImpl authService;

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

    @Override
    public Parking map(String[] source) {
        log.info("Map to parking.");

        String[] temp;
        Parking transaction = new Parking();
        transaction.setCategory(Transaction.Category.PARKING);
        transaction.setLocation(source[0]);

        try {
            log.info("Extract from the 2nd row");
            // 2nd row
            temp = source[2].split("/");
            log.info(source[2]);
            transaction.setLicense(temp[0]);

            log.info("Extract from the 9th row");
            // amount
            transaction.setAmount(Integer.parseInt(source[9].replaceAll("[^\\d]","")));

            if (temp.length > 1) {
                if (temp[1].length() > 3) transaction.setType(Parking.Type.MOTORCYCLE);
//                else if (editDistance(Parking.Type.CAR.name(), temp[1]) >= editDistance(Parking.Type.BUS.name(), temp[1]))
//                    transaction.setType(Parking.Type.CAR);
                transaction.setType(Parking.Type.CAR);
            }

            log.info("Extract from the 6th row");
            transaction.setHours(Integer.parseInt(source[6].replaceAll("[^\\d]","")));

        }   catch (Exception e) {
            log.info(e.getMessage());
        }

        return transaction;
    }

}
