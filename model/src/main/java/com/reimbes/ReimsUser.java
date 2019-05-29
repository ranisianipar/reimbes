package com.reimbes;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Data
public class ReimsUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private long id;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private Role role;

    @OneToMany(mappedBy = "user")
    private Set<Transaction> transactions;
    @Column(updatable = false, nullable = false)
    private long createdAt;

    private Long updatedAt;

    public enum Role {
        ADMIN,
        USER
    }
}
