package com.reimbes.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class ParkingRequest extends TransactionRequest {
    private Date inTime;
    private Date outTime;
}
