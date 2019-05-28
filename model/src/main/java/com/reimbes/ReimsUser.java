package com.reimbes;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;

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

    private long created_at;

    private long updated_at;

    public enum Role {
        ADMIN,
        USER
    }

    public ReimsUser() {
        this.created_at = Instant.now().getEpochSecond();
    }
}
