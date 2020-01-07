package com.reimbes.response;

import com.reimbes.ReimsUser;
import lombok.Data;

import java.util.Date;

@Data
public class UserResponse {
    private long id;
    private String username;
    private ReimsUser.Role role;

    private String license;

    private String vehicle;

    private String division;

    private Date dateOfBirth;

    private ReimsUser.Gender gender;
}
