package com.reimbes.interfaces;


import com.reimbes.Fuel;
import com.reimbes.request.TransactionRequest;

public interface FuelService {
    Fuel create(TransactionRequest request);
}
