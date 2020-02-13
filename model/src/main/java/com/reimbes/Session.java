package com.reimbes;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Table(name = "Sessions")
@Builder
@Data
@Entity
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String token;

    private long expiredTime;

    private String username;

    private ReimsUser.Role role;

    public Session(long id, String token, long expiredTime, String username, ReimsUser.Role role) {
        this.id = id;
        this.token = token;
        this.expiredTime = expiredTime;
        this.username = username;
        this.role = role;
    }

    public Session(){}

}
