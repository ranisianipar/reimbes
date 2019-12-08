package com.reimbes;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Table(name = "Reims_User")
@Entity
@Data @NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ReimsUser extends Patient {

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private Role role;

    @Column
    private Gender gender;

//     ADMIN doesnt need dateOfBirth
//    @Column
//    private Date dateOfBirth;

    @ToString.Exclude
    @OneToMany(mappedBy = "reimsUser")
    private Set<Transaction> transactions;

    @ToString.Exclude
    @OneToMany(mappedBy = "familyMemberOf")
    private Set<FamilyMember> familyMemberOf;

//    @ToString.Exclude
//    @OneToMany(mappedBy = "medicalUser")
//    private Set<Medical> medicals; // dont use json ignore, to still retrieve all medical assigned by user

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

    @Builder(builderMethodName = "ReimsUserBuilder")
    public ReimsUser(long id, String username, String password, Role role, Gender gender, Set<Transaction> transactions,
                     Set<FamilyMember> familyMemberOf, String license, Parking.Type vehicle, Date dateOfBirth) {
        super(id, username, dateOfBirth);

        this.username = username;
        this.password = password;
        this.role = role;
        this.transactions = transactions;
        this.familyMemberOf = familyMemberOf;
        this.license = license;
        this.vehicle = vehicle;
        this.gender = gender;
    }




}
