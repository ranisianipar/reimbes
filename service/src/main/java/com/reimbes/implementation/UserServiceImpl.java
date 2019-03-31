package com.reimbes.implementation;

import com.reimbes.ReimsUser;
import com.reimbes.ReimsUserRepository;
import com.reimbes.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ReimsUserRepository reimsUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public ReimsUser create(ReimsUser user) throws Exception{
        // do validation
        if (reimsUserRepository.findByUsername(user.getUsername()) != null)
            throw new Exception("Username should be unique");

        if (user.getPassword() == null) throw new Exception("Password can't be null");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return reimsUserRepository.save(user);
    }

    @Override
    public ReimsUser getUserByUsername(String username) {
        return reimsUserRepository.findByUsername(username);
    }

    @Override
    public ReimsUser get(long id) {
        return reimsUserRepository.getOne(id);
    }
}
