package com.reimbes;

public interface AdminService {
    ReimsUser createUser(ReimsUser user) throws Exception;
    void deleteUser(long id);
}
