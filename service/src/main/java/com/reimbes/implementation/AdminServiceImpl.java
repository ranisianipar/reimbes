package com.reimbes.implementation;

import com.reimbes.AdminService;
import com.reimbes.ReimsUser;
import com.reimbes.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserServiceImpl userService;

    @Override
    public List<ReimsUser> getAllUser(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @Override
    public ReimsUser getUser(long id) {
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
