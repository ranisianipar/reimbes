package com.reimbes.implementation;

import com.reimbes.AdminService;
import com.reimbes.ReimsUser;
import com.reimbes.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    UserService userService;

    @Override
    public ReimsUser createUser(ReimsUser user) throws Exception{
        return userService.create(user);
    }

    @Override
    public void deleteUser(long id) {
        userService.deleteUser(id);
    }
}
