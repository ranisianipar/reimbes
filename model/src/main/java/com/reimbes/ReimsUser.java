package com.reimbes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Table(name = "Reims_Users")
@Entity
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ReimsUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, insertable = false, columnDefinition = "serial")
    private long id;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private Role role;

    @Column
    private Gender gender;

//     ADMIN doesnt need dateOfBirth
    @Column
    private Date dateOfBirth;

    @OneToMany(mappedBy = "reimsUser")
    @JsonBackReference
    private Set<Transaction> transactions;

    @ToString.Exclude
    @OneToMany(mappedBy = "familyMemberOf")
    private Set<FamilyMember> familyMemberOf;

    @ToString.Exclude
    @OneToMany(mappedBy = "medicalUser")
    private Set<Medical> medicals;

    private String license;

    private Parking.Type vehicle;

    @Column(updatable = false, nullable = false)
    private long createdAt;

    @Column
    private Long updatedAt;

    public enum Role {
        ADMIN,
        USER
    }

    public enum Gender {
        MALE,
        FEMALE
    }
}
