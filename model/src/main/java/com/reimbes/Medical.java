package com.reimbes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Table(name="Medicals")
@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Medical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, insertable = false, columnDefinition = "serial")
    private long id;

    private long amount;
    private long date;

    private long birthDate; // for age calculation
    private ClaimReceipent claimFor;

    private String attachement;


    public enum ClaimReceipent {
        EMPLOYEE,
        FAMILY_MEMBER
    }
}
