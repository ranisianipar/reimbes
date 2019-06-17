package com.reimbes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.reimbes.configuration.ParkingSequenceIdGenerator;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Parking extends Transaction{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parking_seq")
    @GenericGenerator(
            name = "parking_seq",
            strategy = "org.reimbes.configuration.ParkingSequenceIdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = ParkingSequenceIdGenerator.INCREMENT_PARAM, value = "1"),
                    @org.hibernate.annotations.Parameter(name = ParkingSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "PARKING_"),
                    @org.hibernate.annotations.Parameter(name = ParkingSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d") })
    @Column(name = "parking_id")
    private String id;
//
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
