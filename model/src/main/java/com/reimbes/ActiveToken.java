package com.reimbes;

import lombok.Data;

import javax.persistence.*;

@Table(name="Active_Tokens")
@Data
@Entity
public class ActiveToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String token;

    private long expiredTime;

    public ActiveToken(String token) {
        this.token = token;
    }
    public ActiveToken(){}

}
