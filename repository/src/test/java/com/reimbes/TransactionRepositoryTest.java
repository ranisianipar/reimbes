package com.reimbes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Date;

import static com.reimbes.ReimsUser.Role.USER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = TransactionRepository.class)
public class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FuelRepository fuelRepository;

    // need to mock userRepository

    @Autowired
    private ReimsUserRepository userRepository;

    private Fuel fuel;
    private Parking parking;
    private ReimsUser user;

    private Instant now = Instant.now();
    private Date date = new Date();

    @Before
    public void setup() {
        user = ReimsUser.ReimsUserBuilder()
                .id(123)
                .username("user-test")
                .password("user-test123")
                .dateOfBirth(date)
                .gender(ReimsUser.Gender.MALE)
                .createdAt(now.toEpochMilli())
                .role(USER)
                .division("IT")
                .license("xxx 123 yyy")
                .vehicle("Honda apalah")
                .build();
        fuel = new Fuel();
        fuel.setTitle("The fuel");
        fuel.setCategory(Transaction.Category.FUEL);
        fuel.setImage(String.format("/%d/abc.jpg",user.getId()));
        fuel.setType(Fuel.Type.PERTALITE);
        fuel.setLocation("Menteng");
        fuel.setLiters(15);
        fuel.setDate(now.toEpochMilli());
        fuel.setReimsUser(user);
        fuel.setAmount(150000);

        parking = new Parking();
        parking.setTitle("The parking");
        parking.setCategory(Transaction.Category.PARKING);
        parking.setImage(String.format("/%d/pqr.jpg",user.getId()));
        parking.setLocation("Thamrin");
        parking.setCreatedAt(now.toEpochMilli());
        parking.setAmount(21000);

        userRepository.save(user);

        entityManager.persist(fuel);
        entityManager.persist(parking);
    }

    @Test
    public void checkTransactionExistanceByImage() {
//        when(userRepository.save(user)).thenReturn(user);

//        assertTrue(transactionRepository.existsByImage(fuel.getImage()));
//        assertTrue(transactionRepository.existsByImage(parking.getImage()));
//
//        assertFalse(transactionRepository.existsByImage("123/hey.jpg"));

    }



}
