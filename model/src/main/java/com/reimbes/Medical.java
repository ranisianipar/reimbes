package com.reimbes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "reimsUser", nullable = false)
    private ReimsUser reimsUser;


    public enum ClaimReceipent {
        EMPLOYEE,
        FAMILY_MEMBER
    }
}
