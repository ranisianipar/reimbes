package com.reimbes.response;

import com.reimbes.ReimsUser;
import lombok.Data;

@Data
public class UserResponse {
    private long id;
    private String username;
    private ReimsUser.Role role;
}
