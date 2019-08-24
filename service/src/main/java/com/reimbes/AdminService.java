package com.reimbes;

import com.reimbes.exception.ReimsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;

public interface AdminService {
    Page getAllUser(String search, Pageable pageable) throws ReimsException;
    ReimsUser getUser(long id) throws ReimsException;
    ReimsUser createUser(ReimsUser user) throws ReimsException ;

    //  ReimsUser / Login User
    Object updateUser(long id, ReimsUser user, HttpServletResponse response) throws ReimsException;
    void deleteUser(long id);
}
