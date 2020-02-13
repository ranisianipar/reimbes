package com.reimbes.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date dateOfBirth;

    private ReimsUser.Gender gender;
}
