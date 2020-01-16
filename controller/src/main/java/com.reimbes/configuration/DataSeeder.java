package com.reimbes.configuration;

import com.reimbes.FamilyMember;
import com.reimbes.FamilyMemberRepository;
import com.reimbes.ReimsUser;
import com.reimbes.ReimsUserRepository;
import com.reimbes.implementation.UserServiceImpl;
import com.reimbes.implementation.UtilsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;


@Component
public class DataSeeder {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UtilsServiceImpl utils;

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
                    .createdAt(utils.getCurrentTime())
                    .build();
            userService.create(admin);
        }

        if (userRepository.findByUsername("chrisevan") == null) {
            log.info("Seeding data for User 1...");
            ReimsUser user = ReimsUser.ReimsUserBuilder()
                    .username("chrisevan")
                    .password("chrisevan123")
                    .role(ReimsUser.Role.USER)
                    .dateOfBirth(new SimpleDateFormat("dd-MM-yyyy").parse("13-05-1950"))
                    .gender(ReimsUser.Gender.MALE)
                    .vehicle("Mini Cooper")
                    .license("B XXXX YY")
                    .division("Software Engineer")
                    .id(2)
                    .build();
            userService.create(user);
        }

        if (userRepository.findByUsername("ariana") == null) {
            log.info("Seeding data for User 2...");
            ReimsUser user = ReimsUser.ReimsUserBuilder()
                    .username("ariana")
                    .password("ariana123")
                    .role(ReimsUser.Role.USER)
                    .dateOfBirth(new SimpleDateFormat("dd-MM-yyyy").parse("13-05-1996"))
                    .gender(ReimsUser.Gender.FEMALE)
                    .vehicle("Porsche")
                    .license("X YYYY ZZ")
                    .division("Business Development")
                    .id(5)
                    .build();
            userService.create(user);
        }

        if (familyMemberRepository.findByName("galgadot") == null) {
            log.info("Seeding data for Family Member 1...");
            FamilyMember member = FamilyMember.FamilyMemberBuilder()
                    .name("galgadot")
                    .relationship(FamilyMember.Relationship.SPOUSE)
                    .familyMemberOf(userRepository.findByUsername("chrisevan"))
                    .dateOfBirth(new SimpleDateFormat("dd-MM-yyyy").parse("11-11-1950"))
                    .createdAt(utils.getCurrentTime())
                    .id(3)
                    .build();
            familyMemberRepository.save(member);
        }

        if (familyMemberRepository.findByName("trump") == null) {
            log.info("Seeding data for Family Member 2...");
            FamilyMember member = FamilyMember.FamilyMemberBuilder()
                    .name("trump")
                    .relationship(FamilyMember.Relationship.CHILDREN)
                    .familyMemberOf(userRepository.findByUsername("chrisevan"))
                    .dateOfBirth(new SimpleDateFormat("dd-MM-yyyy").parse("13-05-2000"))
                    .createdAt(utils.getCurrentTime())
                    .id(4)
                    .build();
            familyMemberRepository.save(member);
        }

    }
}
