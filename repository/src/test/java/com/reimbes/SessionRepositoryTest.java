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
@SpringBootTest(classes = SessionRepository.class)
public class SessionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SessionRepository sessionRepository;

    private Session token;

    @Before
    public void setup() {
        // given
        token = Session.builder().token("xxx").build();
        entityManager.persistAndFlush(token);
    }

    @Test
    public void whenFindByToken_thenReturnActiveToken() {
        Session wantedToken = sessionRepository.findByToken(token.getToken());
        assertEquals(token, wantedToken);
    }

    @Test
    public void whenDeleteByToken_thenTokenDeleted() {
        Session removeSoonToken = Session.builder().token("remove me please").build();
        entityManager.persistAndFlush(removeSoonToken);

        assertTrue(sessionRepository.existsByToken(removeSoonToken.getToken()));

        sessionRepository.deleteByToken(removeSoonToken.getToken());

        assertFalse(sessionRepository.existsByToken(removeSoonToken.getToken()));

    }

    @Test
    public void whenCheckTokenExistance_thenReturnTokenExistance() {
        assertTrue(sessionRepository.existsByToken(token.getToken()));
        assertFalse(sessionRepository.existsByToken("HAHAHAH"));

    }
}
