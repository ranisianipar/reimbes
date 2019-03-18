package com.reimbes;

public interface UserService {
    ReimsUser login(String username, String password) throws Exception;
}
