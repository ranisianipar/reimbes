package com.reimbes.configuration;

import com.reimbes.FamilyMember;
import com.reimbes.FamilyMemberRepository;
import com.reimbes.ReimsUser;
import com.reimbes.ReimsUserRepository;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.FamilyMemberServiceImpl;
import com.reimbes.implementation.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.reimbes.implementation.Utils.getCurrentTime;

@Component
public class DataSeeder {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private ReimsUserRepository userRepository;

    private static Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @EventListener
    public void seed(ContextRefreshedEvent event) throws Exception {

        if (userRepository.findByUsername("ADMIN") == null) {
            log.info("Seeding data for Super Admin...");
            ReimsUser admin = ReimsUser.ReimsUserBuilder()
                    .username("ADMIN")
                    .password("ADMIN123")
                    .role(ReimsUser.Role.ADMIN)
                    .id(1)
                    .build();
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("iceland") == null) {
            log.info("Seeding data for User 1...");
            ReimsUser user = ReimsUser.ReimsUserBuilder()
                    .username("iceland")
                    .password("iceland123")
                    .role(ReimsUser.Role.USER)
                    .dateOfBirth(new SimpleDateFormat("dd-MM-yyyy").parse("13-05-1950"))
                    .gender(ReimsUser.Gender.MALE)
                    .createdAt(getCurrentTime())
                    .id(2)
                    .build();
            userRepository.save(user);
        }

        if (familyMemberRepository.findByName("swedish house mafia") == null) {
            log.info("Seeding data for Family Member 1...");
            FamilyMember member = FamilyMember.FamilyMemberBuilder()
                    .name("swedish house mafia")
                    .relationship(FamilyMember.Relationship.CHILDREN)
                    .familyMemberOf(userRepository.findByUsername("iceland"))
                    .dateOfBirth(new SimpleDateFormat("dd-MM-yyyy").parse("13-05-2000"))
                    .createdAt(getCurrentTime())
                    .id(3)
                    .build();
            familyMemberRepository.save(member);
        }

    }
}
