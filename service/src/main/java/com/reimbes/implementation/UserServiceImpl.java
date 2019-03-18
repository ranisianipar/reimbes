package com.reimbes.implementation;

import com.reimbes.ReimsUser;
import com.reimbes.UserRepository;
import com.reimbes.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

//    @Autowired
//    PasswordEncoder encoder;

    @Override
    public ReimsUser login(String username, String password) throws Exception {
        ReimsUser reimsUser = userRepository.findByUsername(username);
        if (reimsUser == null) throw new Exception(username);
        else if(password == null) throw new Exception("Password cant be null");

        return reimsUser;
    }
}
