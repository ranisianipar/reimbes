package com.reimbes;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Data
@Builder
@Entity
public class User {

    /*
    * QUESTION: relasi one-to-one buat User ke Admin/Employee? butuh kolom ID yg ngereference ke msg2
    * object
    * MUNGKIN SOLUSI: Bikin abstract class GeneralUser(usern, pass)
    *
    * KENDALA TERBESAR: gmn caranya ngebedain repository tiap role user? biar User.java cuma jadi wadah buat ambil
    * username sama password dari admin/employee repo. Jadi ga ada userRepository
    * */


    @Id
    public String id;

    public String username;
    public String password;

    @Column(nullable = false)
    public Role role;


    public enum Role {
        ADMIN,
        USER
    }

    public User(String id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
