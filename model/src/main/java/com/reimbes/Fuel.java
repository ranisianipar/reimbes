package com.reimbes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Table(name="Fuels")
@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Fuel extends Transaction {

    private long liters;
    private Fuel.Type type;

    public enum Type {
        SOLAR,
        PERTALITE,
        PREMIUM
    }
}
