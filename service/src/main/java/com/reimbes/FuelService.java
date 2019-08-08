package com.reimbes;


import com.reimbes.request.TransactionRequest;

public interface FuelService {
    Fuel create(TransactionRequest request);
    Fuel map(String[] source);
}
