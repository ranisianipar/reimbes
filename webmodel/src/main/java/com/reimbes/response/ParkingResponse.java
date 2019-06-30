package com.reimbes.response;

import lombok.Data;

import java.util.Date;

@Data
public class ParkingResponse extends TransactionResponse{
    private long hours;
}
