package com.reimbes;


public interface UserService {
    ReimsUser create(ReimsUser user) throws Exception;
    ReimsUser getUserByUsername(String username);
    ReimsUser get(long id);
}
