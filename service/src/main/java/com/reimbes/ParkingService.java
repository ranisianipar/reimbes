package com.reimbes;

import com.reimbes.request.TransactionRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ParkingService {
    Parking create(TransactionRequest request);
    Parking map(String[] source);
}
