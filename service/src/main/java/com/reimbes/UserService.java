package com.reimbes;


import com.reimbes.exception.ReimsException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    ReimsUser create(ReimsUser user) throws Exception;
    ReimsUser update(long id, ReimsUser user) throws Exception;
    ReimsUser getUserByUsername(String username);
    ReimsUser get(long id) throws ReimsException;
    List<ReimsUser> getAllUsers(Pageable pageable);
    void deleteUser(long id);
}
