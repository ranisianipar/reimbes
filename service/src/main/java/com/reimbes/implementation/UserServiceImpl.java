package com.reimbes.implementation;

import com.reimbes.User;
import com.reimbes.UserRepository;
import com.reimbes.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

//    @Autowired
//    PasswordEncoder encoder;

    @Override
    public User login(String username, String password) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new Exception(username);
        else if(password == null) throw new Exception("Password cant be null");

        return user;
    }
}
