package com.reimbes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = ReimsUserRepository.class)
public class ReimsUserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReimsUserRepository userRepository;

    private ReimsUser user;


    @Before
    public void setup() {
        user = new ReimsUser();
        user.setId(0);
        user.setPassword("!@#qwe");
        user.setUsername("kjkszpj");
        user.setRole(ReimsUser.Role.ADMIN);
        entityManager.persistAndFlush(user);
    }

    @Test
    public void checkUserExistanceByUsername() {
        assertTrue(userRepository.existsByUsername(user.getUsername()));
    }

    @Test
    public void whenAskedForUserByUsername_thenReturnUser() {
        assertEquals(user, userRepository.findByUsername(user.getUsername()));
    }

    @Test
    public void whenAskedForUserBWithSimiliarAlphabet_thenReturnUsers() {
        Pageable pageable = new PageRequest(0, 100, new Sort(Sort.Direction.DESC, "createdAt"));
        assertEquals(0, userRepository.findByUsernameContainingIgnoreCase("11111",pageable).getTotalElements());
        assertEquals(1, userRepository.findByUsernameContainingIgnoreCase(user.getUsername(),pageable).getTotalElements());
    }


}
