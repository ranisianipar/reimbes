package com.reimbes;

import com.reimbes.exception.ReimsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    Page getAllUser(String search, Pageable pageable) throws ReimsException;
    ReimsUser getUser(long id) throws ReimsException;
    ReimsUser createUser(ReimsUser user) throws ReimsException ;
    ReimsUser updateUser(long id, ReimsUser user) throws ReimsException;
    void deleteUser(long id);
}
