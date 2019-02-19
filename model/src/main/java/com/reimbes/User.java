package com.reimbes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String username;
    private String password;
    private Role role;

    // gimana caranya biar bidirectional? +/-?
//    @OneToMany(mappedBy = "user")
//    private Set<Transaction> transactions;


    public User() {}

    public enum Role {
        ADMIN,
        USER
    }
}

