package com.reimbes;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    List<ReimsUser> getAllUser(Pageable pageable);
    ReimsUser getUser(long id);
    ReimsUser createUser(ReimsUser user) throws Exception;
    ReimsUser updateUser(long id, ReimsUser user) throws Exception;
    void deleteUser(long id);
}
