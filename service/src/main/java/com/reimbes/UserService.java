package com.reimbes;

import org.springframework.stereotype.Service;

public interface UserService {
    User login(String username, String password) throws Exception;
}
