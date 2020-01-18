package com.reimbes;

import com.reimbes.exception.FormatTypeError;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.*;
import org.junit.Before;
import org.junit.Test;
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

import static com.reimbes.Transaction.Category.FUEL;
import static com.reimbes.Transaction.Category.PARKING;
import static com.reimbes.constant.UrlConstants.SUB_FOLDER_TRANSACTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    private ReceiptMapperServiceImpl receiptMapperService;

    @Mock
    private AuthServiceImpl authService;

    @Mock
    private UtilsServiceImpl utilsServiceImpl;

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
        parking.setCategory(PARKING);
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

        when(userService.getUserByUsername(utilsServiceImpl.getPrincipalUsername())).thenReturn(user);

    }

    @Test
    public void errorThrown_whenUserCreateByWrongFormatImage() throws Exception {
        String extension = "png";
        String imageValue = "random string";
        String imagePath = user.getId()+"/123."+extension;

        when(authService.getCurrentUser()).thenReturn(user);
        when(utilsServiceImpl.uploadImage(imageValue, user.getId(), SUB_FOLDER_TRANSACTION)).thenThrow(FormatTypeError.class);

        assertThrows(Exception.class, () -> {
            transactionService.createByImage(imageValue);
        });

    }

    // WHY
    @Test
    public void predictedImageReturn_whenUserCreateTransactionByImage() throws Exception {
        String extension = "png";
        String imageValue = Base64.getEncoder().encodeToString("HAHAHA".getBytes());
        String imagePath = user.getId()+"/123."+extension;

        when(authService.getCurrentUser()).thenReturn(user);
        when(utilsServiceImpl.uploadImage(imageValue, user.getId(), SUB_FOLDER_TRANSACTION)).thenReturn(imagePath);
        when(receiptMapperService.translateImage(imagePath, imageValue)).thenReturn("result");

        Transaction transaction = transactionService.createByImage(imageValue);
        assertEquals(user, transaction.getReimsUser());
        assertEquals(imagePath, transaction.getImage());
        assertNotNull(transaction.getCategory());

    }

    @Test
    public void returnTransaction_whenUserAskedForItById() throws ReimsException {
        when(transactionRepository.findOne(parking.getId())).thenReturn(parking);
        when(authService.getCurrentUser()).thenReturn(user);
        parking.setReimsUser(user);
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
        parking.setReimsUser(user);

        when(transactionRepository.findOne(parking.getId())).thenReturn(parking);
        when(authService.getCurrentUser()).thenReturn(user);

        transactionService.delete(parking.getId());
        verify(utilsServiceImpl, times(1)).removeImage(parking.getImage());
        verify(transactionRepository, times(1)).delete(parking);
    }

    @Test
    public void raisedError_whenRemoveUnregisteredTransaction() throws ReimsException {
        assertThrows(ReimsException.class, () -> {
            transactionService.delete(parking.getId());
        });

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

    
    @Test
    public void returnListOfTransaction_whenUserGetByDateAndType() throws ReimsException {
        Long start; Long end;
        start = end = new Long(10);

        when(authService.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findByReimsUserAndDateBetweenAndCategory(user, start, end, PARKING)).thenReturn(transactions);

        assertEquals(transactions, transactionService.getByDateAndType(start, end, PARKING));
    }

    @Test
    public void returnListOfTransaction_whenUserGetByTyoeAndNullDate() throws ReimsException {
        Long start; Long end;
        start = end = null;

        when(authService.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findByReimsUserAndCategory(user, FUEL)).thenReturn(transactions);

        assertEquals(transactions, transactionService.getByDateAndType(start, end, FUEL));
    }


    @Test
    public void doNothing_whenUserTryToDeleteTransactionByUser_inConditionUserIsNotExist() {
        transactionService.deleteByUser(user);
        verify(transactionRepository).findByReimsUser(user);
        verifyNoMoreInteractions(transactionRepository);
    }

}
