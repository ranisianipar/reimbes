package com.reimbes.configuration;

import com.reimbes.ReimsUser;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder {

    @Autowired
    UserServiceImpl userService;

    private static Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @EventListener
    public void seed(ContextRefreshedEvent event) throws ReimsException {
        if (!userService.isExist("ADMIN")) {
            log.info("Seeding data for Super Admin...");
            ReimsUser user = new ReimsUser();
            user.setRole(ReimsUser.Role.ADMIN);
            user.setId(1); // harusnya ga ada
            user.setUsername("ADMIN");
            user.setPassword("ADMIN123");
            userService.create(user);
        }

    }
}
