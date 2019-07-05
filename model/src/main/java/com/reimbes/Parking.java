package com.reimbes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Parking extends Transaction {

    private int hours;

    public Parking() {
        this.setCreatedAt(Instant.now().getEpochSecond());
    }
}
