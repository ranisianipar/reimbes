package com.reimbes.configuration;


import com.reimbes.User;
import com.reimbes.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataInitiation {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @EventListener
    public void initAdminAccount(ContextRefreshedEvent event){
//        User user = userRepository.findByUsername("root");
//        if (user == null){
//            User newUser = new User();
//            newUser.setId(UUID.randomUUID().toString());
//            newUser.setUsername("root");
//            newUser.setPassword("root");
//            newUser.setRole(User.Role.ADMIN);
//            userRepository.save(newUser);
//        }
    }
}
