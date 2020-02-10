package com.reimbes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Table(name = "Fuels")
@Data
@DiscriminatorValue("FUEL")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Fuel extends Transaction {

    private float liters;
    private long kilometers;
    private Fuel.Type type;

    public enum Type {
        SOLAR,
        PERTALITE,
        PREMIUM
    }
}
