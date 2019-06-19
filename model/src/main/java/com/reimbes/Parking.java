package com.reimbes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Parking extends Transaction{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parking_id")
    private long id;

//    // user one to many transaction
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @JoinColumn(name = "user_id", updatable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    private ReimsUser user;

    private int hours;

    public Parking() {
        this.setCreatedAt(Instant.now().getEpochSecond());
    }
}
