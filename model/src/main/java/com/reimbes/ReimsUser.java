package com.reimbes;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Data
@Entity
public class ReimsUser{

    /*
    * QUESTION: relasi one-to-one buat ReimsUser ke Admin/Employee? butuh kolom ID yg ngereference ke msg2
    * object
    * MUNGKIN SOLUSI: Bikin abstract class GeneralUser(usern, pass)
    *
    * KENDALA TERBESAR: gmn caranya ngebedain repository tiap role user? biar ReimsUser.java cuma jadi wadah buat ambil
    * username sama password dari admin/employee repo. Jadi ga ada userRepository
    * */


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @Column(nullable = false)
    public Role role;


    public enum Role {
        ADMIN,
        USER
    }
}