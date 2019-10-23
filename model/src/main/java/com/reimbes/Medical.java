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

    private String title;

    private long amount;
    private long date;

    private long dateOfBirth; // for age calculation

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JsonManagedReference(value = "patientMedicals")
//    @JoinColumn(name = "patient", nullable = false)
//    private FamilyMember patient;

    private String attachement;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JsonManagedReference(value = "medicalUser")
//    @JoinColumn(name = "reimsUser", nullable = false)
//    private ReimsUser medicalUser;
}
