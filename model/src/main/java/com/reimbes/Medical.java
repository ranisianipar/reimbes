package com.reimbes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="Medicals")
@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Medical {

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
