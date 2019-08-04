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

    // need to mock userRepository

    @Mock
    private ReimsUserRepositoryTest userRepository;

    private Fuel fuel;
    private Parking parking;
    private ReimsUser user;

    @Before
    public void setup() {
        user = new ReimsUser();
        user.setId(123);


        fuel = new Fuel();
        fuel.setTitle("The fuel");
        fuel.setCategory(Transaction.Category.FUEL);
        fuel.setImage(user.getId()+"/abc.jpg");
        fuel.setType(Fuel.Type.PERTALITE);
        fuel.setLiters(15);
        fuel.setDate(1);
        fuel.setReimsUser(user);
        fuel.setAmount(150000);

        parking = new Parking();
        parking.setTitle("The parking");
        parking.setCategory(Transaction.Category.PARKING);
        parking.setImage(user.getId()+"/xyz.jpg");
        parking.setType(Parking.Type.CAR);
        parking.setLocation("Thamrin");
        parking.setLicense("K JKSZ PJ");
        parking.setHours(4);
        parking.setAmount(21000);

        entityManager.persistAndFlush((Transaction) fuel);
        entityManager.persistAndFlush((Transaction) parking);
    }

    @Test
    public void checkTransactionExistanceByImage() {
//        when(userRepository.save(user)).thenReturn(user);

        assertTrue(transactionRepository.existsByImage(fuel.getImage()));
        assertTrue(transactionRepository.existsByImage(parking.getImage()));

        assertFalse(transactionRepository.existsByImage("123/hey.jpg"));
    }



}
