package com.reimbes;


import com.reimbes.exception.ReimsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserService {
    ReimsUser create(ReimsUser user) throws Exception;
    ReimsUser update(long id, ReimsUser user, HttpServletResponse response) throws Exception;
    ReimsUser getUserByUsername(String username);
    ReimsUser get(long id) throws ReimsException;
    Page getAllUsers(String username, Pageable pageable);
    void deleteUser(long id);
}
