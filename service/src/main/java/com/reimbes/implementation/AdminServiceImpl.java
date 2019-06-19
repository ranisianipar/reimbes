package com.reimbes.implementation;

import com.reimbes.AdminService;
import com.reimbes.ReimsUser;
import com.reimbes.UserService;
import com.reimbes.exception.ReimsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserServiceImpl userService;

    @Override
    public Page getAllUser(String search, Pageable pageable) {

        return userService.getAllUsers(search, pageable);
    }

    @Override
    public ReimsUser getUser(long id) throws ReimsException {
        return userService.get(id);
    }

    @Override
    public ReimsUser createUser(ReimsUser user) throws Exception{
        return userService.create(user);
    }

    @Override
    public ReimsUser updateUser(long id, ReimsUser user) throws Exception {
        return userService.update(id, user);
    }

    @Override
    public void deleteUser(long id) {
        userService.deleteUser(id);
    }
}
