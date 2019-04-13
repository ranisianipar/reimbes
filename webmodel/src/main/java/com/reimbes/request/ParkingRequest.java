package com.reimbes.request;

import lombok.Data;

import java.util.Date;

@Data
public class ParkingRequest extends TransactionRequest {
    private Date inTime;
    private Date outTime;
}
