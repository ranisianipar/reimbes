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

    @ToString.Exclude
    @OneToMany(mappedBy = "reimsUser")
    private Set<Transaction> transactions;

    @ToString.Exclude
    @OneToMany(mappedBy = "familyMemberOf")
    private Set<FamilyMember> familyMemberOf;

    private String license;

    private String vehicle;

    private String division;

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
                     Set<FamilyMember> familyMemberOf, String license, String vehicle, Date dateOfBirth, String division,
                     long createdAt, long updatedAt) {
        super(id, username, dateOfBirth, createdAt, updatedAt);

        this.username = username;
        this.password = password;
        this.role = role;
        this.transactions = transactions;
        this.familyMemberOf = familyMemberOf;
        this.license = license;
        this.vehicle = vehicle;
        this.gender = gender;
        this.division = division;
    }




}
