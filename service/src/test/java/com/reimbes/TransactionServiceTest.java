package com.reimbes;

import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.gen5.api.BeforeAll;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.gen5.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = TransactionServiceImpl.class)
public class TransactionServiceTest {

    @Mock
    private ParkingServiceImpl parkingService;

    @Mock
    private FuelServiceImpl fuelService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private AuthServiceImpl authService;

    @Mock
    private Utils utils;

    @Mock
    private TesseractService ocrService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private ReimsUser user;

    private Fuel fuel = new Fuel();
    private Parking parking = new Parking();
    private List transactions = new ArrayList();

    @Before
    public void setup() {
        fuel.setId(new Long(1));
        fuel.setAmount(61000);
        fuel.setReimsUser(user);
        fuel.setDate(Instant.now().getEpochSecond()*1000);
        fuel.setLiters(10);
        fuel.setType(Fuel.Type.PERTALITE);
        fuel.setCategory(Transaction.Category.FUEL);
        fuel.setTitle("FUEL TEST");

        parking.setId(new Long(2));
        parking.setAmount(21000);
        parking.setReimsUser(user);
        parking.setDate(Instant.now().getEpochSecond()*1000);
        parking.setHours(3);
        parking.setLicense("B XXXX YK");
        parking.setLocation("Grha Niaga Thamrin");
        parking.setType(Parking.Type.CAR);
        parking.setCategory(Transaction.Category.PARKING);
        parking.setTitle("PARKING TEST");

        transactions.add(fuel);
        transactions.add(parking);

        user = ReimsUser.ReimsUserBuilder()
                .username("HAHA")
                .password("HEHE")
                .role(ReimsUser.Role.USER)
                .id(1)
                .transactions(new HashSet(transactions))
                .build();

        when(userService.getUserByUsername(utils.getUsername())).thenReturn(user);

    }

    @Test
    public void createByImageTest() throws Exception {
        byte[] imageByte = Base64.getDecoder().decode(("iVBORw".getBytes(StandardCharsets.UTF_8)));
        String extension = "png";
        String imageValue = "data:image/png;base64,iVBORw";
        String imagePath = user.getId()+"/123."+extension;

//        when(userService.getUserByUsername(authService.getCurrentUsername())).thenReturn(medicalUser);
//        when(transactionService.uploadImage(imageByte, extension)).thenReturn(imagePath);
//        when(ocrService.predictImageContent(imageByte)).thenReturn(fuel);
//
//        assertEquals(fuel, transactionService.createByImage(imageValue));

    }

    @Test
    public void returnTransaction_whenUserAskedForItById() throws ReimsException {
        when(transactionRepository.findOne(parking.getId())).thenReturn(parking);
        assertEquals(parking, transactionService.get(parking.getId()));
    }

    @Test
    public void raiseError_whenDserGetTransactionThatDoesntBelongToCurrentUser() {
        user.setId(user.getId()+1);
        parking.setReimsUser(user);

        assertThrows(ReimsException.class, () -> {
           transactionService.get(parking.getId());
        });
    }

    @Test
    public void removeTransaction_whenUserAskedForItById() throws ReimsException {
        when(transactionRepository.findOne(parking.getId())).thenReturn(parking);
        transactionService.delete(parking.getId());
        verify(transactionRepository, times(1)).delete(parking);
    }

    @Test
    public void raisedError_whenRemoveUnregisteredTransaction() throws ReimsException {
        assertThrows(ReimsException.class, () -> {
            transactionService.delete(parking.getId());
        });

    }

    @Test
    public void removeTransactionByUserTest() {
        when(transactionRepository.findByReimsUser(user)).thenReturn(transactions);

//        transactionService.deleteByUser(medicalUser);
//
//        verify(transactionRepository, times(1)).delete(transactions);
    }


    @Test
    public void returnTransactionsOfAUser() {
        when(transactionRepository.findByReimsUser(user)).thenReturn(transactions);
        assertEquals(transactions, transactionService.getByUser(user));
    }

    @Test
    public void returnUserTransactionsFilteredByZeroDate() {
        when(transactionRepository.findByReimsUser(user)).thenReturn(transactions);
        assertEquals(transactions, transactionService.getByUserAndDate(user, 0, 0));
    }

    @Test
    public void returnUserTransactionsFilteredByDate() {
        when(transactionRepository.findByReimsUserAndDateBetween(user,1, 2)).thenReturn(transactions);
        assertEquals(transactions, transactionService.getByUserAndDate(user, 1, 2));
    }

    





}
