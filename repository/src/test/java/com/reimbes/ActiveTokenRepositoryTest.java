package com.reimbes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ActiveTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ActiveTokenRepository activeTokenRepository;

    @Test
    public void whenFindByToken_thenReturnActiveToken() {
        // given
        ActiveToken token = new ActiveToken("xxx");
        entityManager.persist(token);
        entityManager.flush();

        ActiveToken wantedToken = activeTokenRepository.findByToken("xxx");

        
    }
}
