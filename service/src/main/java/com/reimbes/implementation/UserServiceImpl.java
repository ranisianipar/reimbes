package com.reimbes.implementation;

import com.reimbes.User;
import com.reimbes.UserRepository;
import com.reimbes.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public User login(String username, String password) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException(username);
        else if(password == null) throw new Exception("Password cant be null");
        else if (!encoder.encode(password).equals(user.getPassword()))
            throw new Exception("Username and Password isn't match");
        return user;
    }
}
