package com.reimbes.interfaces;


import com.reimbes.ReimsUser;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;

public interface UserService {
    ReimsUser create(ReimsUser user) throws ReimsException;
    ReimsUser update(long id, ReimsUser user) throws ReimsException;
    ReimsUser updateMyData(ReimsUser newData, String token) throws ReimsException;
    ReimsUser getUserByUsername(String username);
    ReimsUser get(long id) throws ReimsException;
    Page getAllUsers(String username, Pageable pageable);
    boolean delete(long id);
    boolean isExist(String username);
    String getReport(Long start, Long end, String reimbursementType) throws Exception;
    String getImage(String imagePath) throws ReimsException;
    boolean changePassword(String password) throws NotFoundException;
}
