package com.reimbes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Fuel extends Transaction {

    private long liters;

    public Fuel() {
        this.setCreatedAt(Instant.now().getEpochSecond());
    }
}
