package com.reimbes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Table(name = "Reims_Users")
@Entity
@Data
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

    @OneToMany(mappedBy = "reimsUser")
    @JsonBackReference
    private Set<Transaction> transactions;

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
