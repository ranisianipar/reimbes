package com.reimbes.interfaces;


import com.reimbes.ReimsUser;
import com.reimbes.exception.ReimsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;

public interface UserService {
    ReimsUser create(ReimsUser user) throws ReimsException;
    ReimsUser update(long id, ReimsUser user) throws ReimsException;
    ReimsUser updateMyData(ReimsUser newData, HttpServletResponse response) throws ReimsException;
    ReimsUser getUserByUsername(String username);
    ReimsUser get(long id) throws ReimsException;
    Page getAllUsers(String username, Pageable pageable);
    void delete(long id);
    boolean isExist(String username);
    byte[] getReport(Long start, Long end, String reimbursementType) throws Exception;
    byte[] getImage(String imagePath) throws ReimsException;
}
