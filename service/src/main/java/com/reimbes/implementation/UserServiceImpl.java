package com.reimbes.implementation;

import com.reimbes.ReimsUser;
import com.reimbes.UserRepository;
import com.reimbes.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ReimsUser login(String username, String password) throws Exception {
        ReimsUser reimsUser = userRepository.findByUsername(username);
        if (reimsUser == null) throw new Exception(username);

        else if(password == null) throw new Exception("Password cant be null");

        return reimsUser;
    }

    @Override
    public ReimsUser create(ReimsUser user) throws Exception{
        // do validation
        if (userRepository.findByUsername(user.getUsername()) != null)
            throw new Exception("Username should be unique");

        if (user.getPassword() == null) throw new Exception("Password can't be null");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
