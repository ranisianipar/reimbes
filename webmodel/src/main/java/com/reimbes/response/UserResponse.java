package com.reimbes.response;

import com.reimbes.ReimsUser;
import lombok.Data;

@Data
public class UserResponse {
    public String username;
    public ReimsUser.Role role;
}
