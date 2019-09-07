package com.reimbes.implementation;

import com.reimbes.AdminService;
import com.reimbes.ReimsUser;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class AdminServiceImpl implements AdminService {
    private static Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Utils utils;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public Page getAllUser(String search, Pageable pageRequest) throws ReimsException {
        log.info("Get all user by: "+utils.getUsername());

        log.info("Page request number: "+pageRequest.getPageNumber());
        // tha page number default is 1, but querying things start from 0.
        int index = pageRequest.getPageNumber()-1;
        if (index < 0) throw new NotFoundException("Page with negative index");
        Pageable pageable = new PageRequest(index, pageRequest.getPageSize(), pageRequest.getSort());

        return userService.getAllUsers(search, pageable);
    }

    @Override
    public ReimsUser getUser(long id) throws ReimsException {
        return userService.get(id);
    }

    @Override
    public ReimsUser createUser(ReimsUser user) throws ReimsException {
        if (user.getRole() == ReimsUser.Role.USER && user.getGender() == null)
            throw new DataConstraintException("NULL_ATTRIBUTE_GENDER");
        return userService.create(user);
    }

    @Override
    public ReimsUser updateUser(long id, ReimsUser user, HttpServletResponse response) throws ReimsException {
        ReimsUser currentUser = userService.getUserByUsername(utils.getUsername());

        if (user.getRole() == ReimsUser.Role.USER && user.getGender() == null)
            throw new DataConstraintException("NULL_ATTRIBUTE_GENDER");

        // if admin try to update his data
        if (currentUser.getId() == id) return userService.updateMyData(user, response);

        return userService.update(id, user);
    }

    @Override
    public void deleteUser(long id) throws ReimsException{
        ReimsUser currentUser = userService.getUserByUsername(utils.getUsername());
        if (currentUser.getId() == id) throw new ReimsException("SELF_DELETION", HttpStatus.METHOD_NOT_ALLOWED, 405);
        userService.deleteUser(id);
    }
}
