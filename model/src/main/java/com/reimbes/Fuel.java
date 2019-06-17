package com.reimbes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.reimbes.configuration.FuelSequenceIdGenerator;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Fuel extends Transaction {

    /*
    * source:
    * https://thoughts-on-java.org/custom-sequence-based-idgenerator/
    * */

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fuel_seq")
    @GenericGenerator(
            name = "fuel_seq",
            strategy = "org.reimbes.configuration.FuelSequenceIdGenerator",
            parameters = {
                    @Parameter(name = FuelSequenceIdGenerator.INCREMENT_PARAM, value = "1"),
                    @Parameter(name = FuelSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "FUEL_"),
                    @Parameter(name = FuelSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d") })
    @Column(name = "fuel_id")
    private String id;
//
//    // user one to many transaction
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @JoinColumn(name = "user_id", updatable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    private ReimsUser user;

    private long liters;

    public Fuel() {
        this.setCreatedAt(Instant.now().getEpochSecond());
    }
}
