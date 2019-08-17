package com.reimbes.response;

import lombok.Data;

@Data
public class LoginResponse extends UserResponse {
    private String authorization;
}
