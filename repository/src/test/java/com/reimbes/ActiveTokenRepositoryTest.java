package com.reimbes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = ActiveTokenRepository.class)
public class ActiveTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ActiveTokenRepository activeTokenRepository;

    private ActiveToken token;

    @Before
    public void setup() {
        // given
        token = new ActiveToken("xxx");
        entityManager.persistAndFlush(token);
    }

    @Test
    public void whenFindByToken_thenReturnActiveToken() {
        ActiveToken wantedToken = activeTokenRepository.findByToken("xxx");
        assertEquals(token, wantedToken);
    }

    @Test
    public void whenDeleteByToken_thenTokenDeleted() {

        ActiveToken removeSoonToken = new ActiveToken("remove me please");
        entityManager.persistAndFlush(removeSoonToken);

        assertTrue(activeTokenRepository.existsByToken(removeSoonToken.getToken()));

        activeTokenRepository.deleteByToken(removeSoonToken.getToken());

        assertFalse(activeTokenRepository.existsByToken(removeSoonToken.getToken()));

    }

    @Test
    public void whenCheckTokenExistance_thenReturnTokenExistance() {
        assertTrue(activeTokenRepository.existsByToken("xxx"));
        assertFalse(activeTokenRepository.existsByToken("HAHAHAH"));

    }
}
