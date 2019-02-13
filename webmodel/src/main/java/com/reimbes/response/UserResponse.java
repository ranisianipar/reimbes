package com.reimbes.response;

import com.reimbes.User;
import lombok.Data;

@Data
public class UserResponse {

    private String username;

    UserResponse(User user) {
        this.username = user.getUsername();
    }

}
