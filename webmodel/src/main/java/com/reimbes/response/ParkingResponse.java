package com.reimbes.response;

import com.reimbes.Parking;
import lombok.Data;

import java.util.Date;

@Data
public class ParkingResponse extends TransactionResponse {
    private long hours;
    private String location;
    private String license;
}
