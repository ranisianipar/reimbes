package com.reimbes;

import com.reimbes.request.TransactionRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ParkingService {
    Parking get(long id);
    List<Parking> getByUser(ReimsUser user, Pageable page);
    void delete(long id);
//    Parking create(Parking transaction);
}
