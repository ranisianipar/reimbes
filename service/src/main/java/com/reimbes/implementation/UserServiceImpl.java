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
    private ReimsUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public ReimsUser create(ReimsUser user) throws Exception{
        // do validation
        if (userRepository.findByUsername(user.getUsername()) != null)
            throw new Exception("Username should be unique");

        if (user.getPassword() == null) throw new Exception("Password can't be null");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public ReimsUser getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public ReimsUser get(long id) {
        return userRepository.getOne(id);
    }

    @Override
    public void deleteUser(long id) {
//        ReimsUser user = userRepository.findOne(id);
        userRepository.delete(id);
    }
}
