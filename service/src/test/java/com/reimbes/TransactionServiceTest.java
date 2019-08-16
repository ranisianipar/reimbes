package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.gen5.api.BeforeAll;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.*;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

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
    private TesseractService ocrService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private ReimsUser user = new ReimsUser();
    private Fuel fuel = new Fuel();
    private Pageable pageRequest = new PageRequest(1, 5, new Sort(Sort.Direction.DESC, "createdAt"));
    private Pageable pageForQuery = new PageRequest(0, pageRequest.getPageSize(), pageRequest.getSort());
    private Parking parking = new Parking();
    private List transactions = new ArrayList();
    public static File testFolder;

    @BeforeClass
    public static void init() throws  Exception {
        System.out.println("Do initiation");
        testFolder = new File(UrlConstants.IMAGE_FOLDER_PATH+"test/");
        System.out.println("EXIST? " + testFolder.exists());
        if (!testFolder.exists()) {
            testFolder.mkdir();
        }


    }

//    @AfterClass
//    public static void clear() throws Exception{
//        Files.delete(Paths.get(UrlConstants.IMAGE_FOLDER_PATH+"test/"));
//    }

    @Before
    public void setup() {
        user.setUsername("HAHA");
        user.setPassword("HEHE");
        user.setRole(ReimsUser.Role.USER);
        user.setId(1);

        fuel.setId(new Long(2 ));
        fuel.setAmount(61000);
        fuel.setReimsUser(user);
        fuel.setDate(Instant.now().getEpochSecond()*1000);
        fuel.setLiters(10);
        fuel.setType(Fuel.Type.PERTALITE);
        fuel.setCategory(Transaction.Category.FUEL);
        fuel.setTitle("FUEL TEST");
        fuel.setImage("test"+"/"+fuel.getId()+"-"+ UUID.randomUUID()+".jpg");

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
        parking.setImage("test/"+parking.getId()+"/"+ UUID.randomUUID()+".jpg");

        transactions.add(fuel);
        transactions.add(parking);

        user.setTransactions(new HashSet(transactions));

        when(userService.getUserByUsername(authService.getCurrentUsername())).thenReturn(user);

    }

    @Test
    public void createByImageTest() throws Exception {
        byte[] imageByte = Base64.getDecoder().decode(("iVBORw".getBytes(StandardCharsets.UTF_8)));
        String extension = "png";
        String imageValue = "data:image/png;base64,iVBORw";
        String imagePath = user.getId()+"/123."+extension;

//        when(userService.getUserByUsername(authService.getCurrentUsername())).thenReturn(user);
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
    public void removeNoTransaction_whenUserDoesntHaveAnyTransaction() {

        transactionService.deleteByUser(user);

        verify(transactionRepository, times(0)).delete(transactions);
    }

    @Test
    public void removeTransactionsByUser() {
        when(transactionRepository.findByReimsUser(user)).thenReturn(transactions);

        transactionService.deleteByUser(user);

        verify(transactionRepository, times(1)).delete(transactions);

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
    public void returnAllUserTransactions_whenFilterWithNoCategory() throws ReimsException {
        Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));


        long timeMock = Instant.now().toEpochMilli();

        Page result = new PageImpl(transactions);
        when(transactionRepository.findByReimsUserAndTitleContainingIgnoreCaseAndDateBetween(
                user,
                "",
                timeMock,
                timeMock,
                pageForQuery
        )).thenReturn(result);

        assertEquals(result, transactionService.getAll(pageForQuery,
                timeMock+"",
                timeMock+"",
                null,
                null));
    }

    @Test
    public void returnAllUserTransactions_whenFilterWithUnwanterDateFormat() {
        assertThrows(ReimsException.class, () ->
                transactionService.getAll(pageForQuery,
                "awgaweg",
                "1110",
                null,
                null));
    }

    @Test
    public void returnAllUserTransactions_whenFilterWithNullDate() throws ReimsException {

        String title = "";

        Page result = new PageImpl(transactions);

        when(transactionRepository.findByReimsUserAndTitleContainingIgnoreCaseAndCategory(user,
                title, Transaction.Category.PARKING, pageForQuery)).thenReturn(result);

        assertEquals(result, transactionService.getAll(pageForQuery,
                "",
                "",
                null,
                Transaction.Category.PARKING));
    }

    @Test
    public void returnAllUserTransactions_whenFilterWithNullDateAndNullCategory() throws ReimsException {

        String title = "";
        Page result = new PageImpl(transactions);
        when(transactionRepository.findByReimsUserAndTitleContainingIgnoreCase(user, title, pageForQuery)).thenReturn(result);

        assertEquals(result, transactionService.getAll(pageForQuery,
                "",
                "",
                null,
                null));
    }

    @Test
    public void returnAllUserTransactions_whenAllFilterParamsNotNull() throws ReimsException {

        Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        long timeMock = Instant.now().toEpochMilli();

        String title = "jajaja";


        Page result = new PageImpl(transactions);
        when(transactionRepository.findByReimsUserAndTitleContainingIgnoreCaseAndCategoryAndDateBetween(
                user,
                title,
                Transaction.Category.PARKING,
                timeMock,
                timeMock,
                pageForQuery
        )).thenReturn(result);

        assertEquals(result, transactionService.getAll(pageRequest,
                timeMock+"",
                timeMock+"",
                title,
                Transaction.Category.PARKING));
    }







}
