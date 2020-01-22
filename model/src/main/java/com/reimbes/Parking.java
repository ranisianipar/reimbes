package com.reimbes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;


@Table(name = "Parkings")
@Data
@DiscriminatorValue("PARKING")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Parking extends Transaction {

    private int hours;

    private String license;
    private String location;

    private Parking.Type type;

    public enum Type {
        CAR,
        BUS,
        MOTORCYCLE
    }
}
