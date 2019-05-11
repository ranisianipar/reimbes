package com.reimbes;

import lombok.Data;

import java.util.Date;

@Data
public abstract class Transaction {

    // standard crud attributes
    private Date created_at;
    private long amount;
    private String image;

    private ReimsUser user;

    private Date date;

    private Category category;

    public enum Category {
        FUEL,
        PARKING
    }

}
