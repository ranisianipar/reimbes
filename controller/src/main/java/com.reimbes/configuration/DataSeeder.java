package com.reimbes.configuration;

import com.reimbes.ReimsUser;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder {

    @Autowired
    UserServiceImpl userService;

    @EventListener
    public void seed(ContextRefreshedEvent event) throws ReimsException {
        ReimsUser user = userService.getUserByUsername("ADMIN");
        if (user == null) {
            user = new ReimsUser();
            user.setRole(ReimsUser.Role.ADMIN);
            user.setId(1);
            user.setUsername("ADMIN");
            user.setPassword("ADMIN123");
            userService.create(user);
        }

    }
}
