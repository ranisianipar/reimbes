package com.reimbes;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class ReimsUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @Column(nullable = false)
    private Role role;


    public enum Role {
        ADMIN,
        USER
    }
}
