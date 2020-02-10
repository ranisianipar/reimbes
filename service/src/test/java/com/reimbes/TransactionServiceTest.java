package com.reimbes;

import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.FormatTypeError;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.TransactionServiceImpl;
import com.reimbes.interfaces.AuthService;
import com.reimbes.interfaces.ReceiptMapperService;
import com.reimbes.interfaces.UserService;
import com.reimbes.interfaces.UtilsService;
import com.reimbes.request.TransactionRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.*;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;

import static com.reimbes.Transaction.Category.FUEL;
import static com.reimbes.Transaction.Category.PARKING;
import static com.reimbes.constant.General.DEFAULT_LONG_VALUE;
import static com.reimbes.constant.UrlConstants.SUB_FOLDER_TRANSACTION;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
import static org.junit.gen5.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = TransactionServiceImpl.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private ReceiptMapperService receiptMapperService;

    @Mock
    private AuthService authService;

    @Mock
    private UtilsService utilsService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private ReimsUser user;

    private Fuel fuel = new Fuel();
    private Parking parking = new Parking();
    private List transactions = new ArrayList();

    private Pageable pageRequest = new PageRequest(1, 5, new Sort(Sort.Direction.DESC, "createdAt"));
    private Pageable pageForQuery = new PageRequest(0, pageRequest.getPageSize(), pageRequest.getSort());

    @After
    public void tearDown() {
        verifyNoMoreInteractions(transactionRepository, userService, receiptMapperService, authService, utilsService);
    }

    @Before
    public void setup() {
        fuel.setId(new Long(1));
        fuel.setAmount(61000);
        fuel.setCreatedAt(Instant.now().toEpochMilli());
        fuel.setReimsUser(user);
        fuel.setDate(Instant.now().toEpochMilli());
        fuel.setLiters(10);
        fuel.setLocation("DKI Jakarta");
        fuel.setType(Fuel.Type.PERTALITE);
        fuel.setCategory(Transaction.Category.FUEL);
        fuel.setTitle("FUEL_VALUE TEST");

        parking.setId(new Long(2));
        parking.setAmount(21000);
        parking.setReimsUser(user);
        parking.setDate(Instant.now().getEpochSecond() * 1000);
        parking.setLocation("Grha Niaga Thamrin");
        parking.setCategory(PARKING);
        parking.setTitle("PARKING_VALUE TEST");

        user = ReimsUser.ReimsUserBuilder()
                .username("HAHA")
                .password("HEHE")
                .role(ReimsUser.Role.USER)
                .id(1)
                .transactions(new HashSet(transactions))
                .build();
    }

    @Test
    public void errorThrown_whenUserCreateByWrongFormatImage() throws Exception {
        String imageValue = "random string";
        Transaction transactionRequst = new Transaction();
        transactionRequst.setImage(imageValue);

        when(authService.getCurrentUser()).thenReturn(user);
        when(utilsService.uploadImage(imageValue, user.getId(), SUB_FOLDER_TRANSACTION)).thenThrow(FormatTypeError.class);

        assertThrows(FormatTypeError.class, () -> {
            transactionService.createByImageAndCategory(transactionRequst);
        });

        verify(authService).getCurrentUser();
        verify(utilsService).uploadImage(imageValue, user.getId(), SUB_FOLDER_TRANSACTION);
    }

    @Test
    public void predictedImageReturn_whenUserCreateTransactionByImage() throws Exception {
        String extension = "png";
        String imageValue = Base64.getEncoder().encodeToString("HAHAHA".getBytes());
        String imagePath = user.getId() + "/123." + extension;

        Transaction transactionRequest = new Transaction();
        transactionRequest.setImage(imageValue);

        when(authService.getCurrentUser()).thenReturn(user);
        when(utilsService.uploadImage(imageValue, user.getId(), SUB_FOLDER_TRANSACTION)).thenReturn(imagePath);
        when(receiptMapperService.map(transactionRequest)).thenReturn(fuel);

        Transaction transaction = transactionService.createByImageAndCategory(transactionRequest);

        assertEquals(user, transaction.getReimsUser());
        assertEquals(imagePath, transaction.getImage());
        assertNotNull(transaction.getCategory());

        verify(authService).getCurrentUser();
        verify(utilsService).uploadImage(imageValue, user.getId(), SUB_FOLDER_TRANSACTION);
        verify(receiptMapperService).map(transactionRequest);

    }

    @Test
    public void returnTransaction_whenUserAskedForItById() throws ReimsException {
        when(transactionRepository.findOne(parking.getId())).thenReturn(parking);
        when(authService.getCurrentUser()).thenReturn(user);
        parking.setReimsUser(user);

        assertEquals(parking, transactionService.get(parking.getId()));

        verify(authService).getCurrentUser();
        verify(transactionRepository).findOne(parking.getId());

    }

    @Test
    public void raiseError_whenDserGetTransactionThatDoesntBelongToCurrentUser() throws ReimsException {
        user.setId(user.getId() + 1);
        parking.setReimsUser(user);

        assertThrows(ReimsException.class, () -> {
            transactionService.get(parking.getId());
        });

        verify(authService).getCurrentUser();
        verify(transactionRepository).findOne(parking.getId());
    }

    @Test
    public void removeTransaction_whenUserAskedForItById() throws ReimsException {
        parking.setReimsUser(user);

        when(transactionRepository.findOne(parking.getId())).thenReturn(parking);
        when(authService.getCurrentUser()).thenReturn(user);

        boolean result = transactionService.delete(parking.getId());

        verify(authService).getCurrentUser();
        verify(transactionRepository).findOne(parking.getId());
        verify(utilsService).removeImage(parking.getImage());
        verify(transactionRepository).delete(parking);
        assertTrue(result);
    }

    @Test
    public void raisedError_whenRemoveUnregisteredTransaction() throws NotFoundException {
        assertThrows(ReimsException.class, () -> {
            transactionService.delete(parking.getId());
        });
        verify(authService).getCurrentUser();
        verify(transactionRepository).findOne(parking.getId());
    }


    @Test
    public void returnTransactionsOfAUser() {
        List<Transaction> result;
        when(transactionRepository.findByReimsUser(user)).thenReturn(transactions);
        result = transactionService.getByUser(user);
        verify(transactionRepository).findByReimsUser(user);
        assertEquals(transactions, result);
    }

    @Test
    public void returnUserTransactionsFilteredByZeroDate() {
        List<Transaction> result;
        when(transactionRepository.findByReimsUser(user)).thenReturn(transactions);
        result = transactionService.getByUserAndDate(user, 0, 0);
        verify(transactionRepository).findByReimsUser(user);
        assertEquals(transactions, result);
    }

    @Test
    public void returnUserTransactionsFilteredByDate() {
        List<Transaction> result;
        long start = 1;
        long end = 2;
        when(transactionRepository.findByReimsUserAndDateBetween(user, start, end)).thenReturn(transactions);
        result = transactionService.getByUserAndDate(user, start, end);
        verify(transactionRepository).findByReimsUserAndDateBetween(user, start, end);
        assertEquals(transactions, result);
    }


    @Test
    public void returnListOfTransaction_whenUserGetByDateAndCategory() throws ReimsException {
        List<Transaction> result;
        long start = 1;
        long end = 2;
        when(authService.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findByReimsUserAndDateBetweenAndCategory(user, start, end, PARKING)).thenReturn(transactions);
        result = transactionService.getByDateAndCategory(start, end, PARKING);
        verify(authService).getCurrentUser();
        verify(transactionRepository).findByReimsUserAndDateBetweenAndCategory(user, start, end, PARKING);
        assertEquals(transactions, result);
    }

    @Test
    public void returnListOfTransaction_whenUserGetByTypeAndNullDate() throws ReimsException {
        List<Transaction> result;
        Long start = DEFAULT_LONG_VALUE;
        Long end = DEFAULT_LONG_VALUE;
        when(authService.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findByReimsUserAndCategory(user, FUEL)).thenReturn(transactions);
        result = transactionService.getByDateAndCategory(start, end, FUEL);
        verify(authService).getCurrentUser();
        verify(transactionRepository).findByReimsUserAndCategory(user, FUEL);
        assertEquals(transactions, result);
    }


    @Test
    public void doNothing_whenUserTryToDeleteTransactionByUser_inConditionUserIsNotExist() {
        when(transactionRepository.findByReimsUser(user)).thenReturn(null);

        boolean result = transactionService.deleteTransactionImageByUser(user);
        verify(transactionRepository).findByReimsUser(user);
        assertFalse(result);
    }

    @Test
    public void deleteTransactionImageByUserSuccessfully() {
        transactions.add(fuel);
        when(transactionRepository.findByReimsUser(user)).thenReturn(transactions);
        boolean result = transactionService.deleteTransactionImageByUser(user);

        verify(transactionRepository).findByReimsUser(user);
        verify(utilsService).removeImage(fuel.getImage());
        assertTrue(result);

    }

    @Test
    public void returnPageOfTransactions_whenUserGetAllTransactionsWIthCategoryAndNoDateRange() throws ReimsException {
        transactions.add(fuel);
        transactions.add(parking);
        String title = "";
        String start;
        String end;
        start = end = "";
        Transaction.Category category = PARKING;

        Page expectedResult = new PageImpl<>(transactions);
        Page result;

        when(authService.getCurrentUser()).thenReturn(user);
        when(utilsService.getPageRequest(pageRequest)).thenReturn(pageForQuery);
        when(transactionRepository.findByReimsUserAndTitleContainingIgnoreCaseAndCategory(user, title, category, pageForQuery))
                .thenReturn(expectedResult);

        result = transactionService.getAll(pageRequest, title, start, end, category);
        verify(authService).getCurrentUser();
        verify(utilsService).getPageRequest(pageRequest);
        verify(transactionRepository).findByReimsUserAndTitleContainingIgnoreCaseAndCategory(user, title, category, pageForQuery);
        assertEquals(expectedResult, result);
    }

    @Test
    public void returnPageOfTransactions_whenUserGetAllTransactionsWIthNoCategoryAndNoDateRange() throws ReimsException {
        String title = "";
        String start;
        String end;
        start = end = "";
        Transaction.Category category = null;

        Page expectedResult = new PageImpl(transactions);
        Page result;

        when(authService.getCurrentUser()).thenReturn(user);
        when(utilsService.getPageRequest(pageRequest)).thenReturn(pageForQuery);
        when(transactionRepository.findByReimsUserAndTitleContainingIgnoreCase(user, title, pageForQuery)).thenReturn(expectedResult);

        result = transactionService.getAll(pageRequest, title, start, end, category);
        verify(authService).getCurrentUser();
        verify(utilsService).getPageRequest(pageRequest);
        verify(transactionRepository).findByReimsUserAndTitleContainingIgnoreCase(user, title, pageForQuery);
        assertEquals(expectedResult, result);
    }

    @Test
    public void returnPageOfTransactions_whenUserGetAllTransactionsWIthDateRangeAndNoCategory() throws ReimsException {
        String title = "";
        String startDate;
        String endDate;
        startDate = endDate = "123";

        Long start;
        Long end;
        start = end = new Long(endDate);

        Transaction.Category category = null;

        Page expectedResult = new PageImpl<>(transactions);
        Page result;

        when(authService.getCurrentUser()).thenReturn(user);
        when(utilsService.getPageRequest(pageRequest)).thenReturn(pageForQuery);
        when(transactionRepository.findByReimsUserAndTitleContainingIgnoreCaseAndDateBetween(user, title, start, end, pageForQuery))
                .thenReturn(expectedResult);

        result = transactionService.getAll(pageRequest, title, startDate, endDate, category);
        verify(authService).getCurrentUser();
        verify(utilsService).getPageRequest(pageRequest);
        verify(transactionRepository).findByReimsUserAndTitleContainingIgnoreCaseAndDateBetween(user, title, start, end, pageForQuery);
        assertEquals(expectedResult, result);
    }

    @Test
    public void returnPageOfTransactions_whenUserGetAllTransactionsWIthDateRangeAndCategory() throws ReimsException {
        transactions.add(fuel);
        transactions.add(parking);
        String title = "";
        String startDate;
        String endDate;
        startDate = endDate = "123";

        Long start;
        Long end;
        start = end = new Long(endDate);

        Transaction.Category category = FUEL;

        Page expectedResult = new PageImpl<>(transactions);
        Page result;

        when(authService.getCurrentUser()).thenReturn(user);
        when(utilsService.getPageRequest(pageRequest)).thenReturn(pageForQuery);
        when(transactionRepository.findByReimsUserAndTitleContainingIgnoreCaseAndCategoryAndDateBetween(user, title, category, start, end, pageForQuery))
                .thenReturn(expectedResult);

        result = transactionService.getAll(pageRequest, title, startDate, endDate, category);
        verify(authService).getCurrentUser();
        verify(utilsService).getPageRequest(pageRequest);
        verify(transactionRepository).findByReimsUserAndTitleContainingIgnoreCaseAndCategoryAndDateBetween(user, title, category, start, end, pageForQuery);
        assertEquals(expectedResult, result);
    }

    @Test
    public void errorThrown_whenUserGetAllTransactionsWithInvalidDateRange() throws ReimsException {
        String title = "";
        String startDate;
        String endDate;
        startDate = endDate = "xys";

        when(authService.getCurrentUser()).thenReturn(user);
        when(utilsService.getPageRequest(pageRequest)).thenReturn(pageForQuery);

        assertThrows(FormatTypeError.class, () -> transactionService.getAll(pageRequest, title, startDate, endDate, null));
        verify(authService).getCurrentUser();
        verify(utilsService).getPageRequest(pageRequest);
    }

    @Test
    public void returnUpdatedFuel_whenUserInputValidData() throws ReimsException {
        long now = Instant.now().toEpochMilli();

        List<String> images = new ArrayList<>();
        images.add("hahaha/123.jpg");

        TransactionRequest request = new TransactionRequest();
        request.setTitle("TEST");
        request.setAttachments(images);
        request.setCategory(fuel.getCategory());
        request.setDate(fuel.getDate());
        request.setAmount(fuel.getAmount());
        request.setLiters(fuel.getLiters());
        request.setFuelType(fuel.getType());

        fuel.setImage(request.getAttachments().get(0)); // update fuel attachments as in request
        fuel.setTitle(request.getTitle()); // update fuel title as in request

        when(utilsService.isFileExists(request.getAttachments().get(0))).thenReturn(true);
        when(utilsService.getCurrentTime()).thenReturn(now);
        when(authService.getCurrentUser()).thenReturn(user);
        when(transactionRepository.save(fuel)).thenReturn(fuel);

        Transaction expectedResult = new Fuel();
        expectedResult.setReimsUser(user);
        expectedResult.setAmount(request.getAmount());
        expectedResult.setCategory(request.getCategory());
        ((Fuel) expectedResult).setLiters(fuel.getLiters());
        expectedResult.setDate(fuel.getDate());
        expectedResult.setImage(request.getAttachments().get(0));
        ((Fuel) expectedResult).setType(fuel.getType());
        expectedResult.setTitle(request.getTitle());

        Transaction result = transactionService.update(fuel);
        verify(authService).getCurrentUser();

        // validation
        verify(utilsService).isFileExists(fuel.getImage());
        verify(transactionRepository).existsByImage(fuel.getImage());

        verify(utilsService).getCurrentTime();
        verify(transactionRepository).save(fuel);
        assertEquals(expectedResult, result);
    }

    @Test
    public void returnUpdatedParking_whenUserInputValidData() throws ReimsException {
        long now = Instant.now().toEpochMilli();
        List<String> images = new ArrayList<>();
        images.add(String.format("/%d/transaction/123.jpg", user.getId()));

        TransactionRequest request = new TransactionRequest();
        request.setTitle("test");
        request.setAttachments(images);
        request.setCategory(parking.getCategory());
        request.setDate(parking.getDate());
        request.setAmount(parking.getAmount());
        request.setDate(parking.getDate());

        parking.setImage(request.getAttachments().get(0)); // update fuel attachments as in request
        parking.setTitle(request.getTitle()); // update fuel title as in request

        when(utilsService.isFileExists(request.getAttachments().get(0))).thenReturn(true);
        when(utilsService.getCurrentTime()).thenReturn(now);
        when(authService.getCurrentUser()).thenReturn(user);
        when(transactionRepository.save(parking)).thenReturn(parking);

        Transaction expectedResult = new Parking();
        expectedResult.setReimsUser(user);
        expectedResult.setAmount(request.getAmount());
        expectedResult.setCategory(request.getCategory());
        expectedResult.setDate(parking.getDate());
        expectedResult.setImage(request.getAttachments().get(0));
        expectedResult.setLocation(parking.getLocation());

        System.out.println("[TEST] " + request);
        Transaction result = transactionService.update(parking);
        verify(authService).getCurrentUser();

        // validation
        verify(utilsService).isFileExists(parking.getImage());
        verify(transactionRepository).existsByImage(parking.getImage());

        verify(utilsService).getCurrentTime();
        verify(transactionRepository).save(parking);
        assertEquals(expectedResult, result);
    }


    @Test
    public void errorThrown_whenUserUpdateParkingWithInvalidData() throws NotFoundException {
        Transaction request = new Parking();
        request.setImage("hahaha/123.jpg");
        request.setCategory(PARKING);
        request.setDate(0);
        request.setAmount(0);

        assertThrows(DataConstraintException.class, () -> transactionService.update(request));

        // validation
        verify(utilsService).isFileExists(request.getImage());
        verify(transactionRepository).existsByImage(request.getImage());
        verify(utilsService).getCurrentTime();

        verify(authService).getCurrentUser();

    }

    @Test
    public void errorThrown_whenUserUpdateFuelWithInvalidData() throws NotFoundException {
        Transaction request = new Fuel();
        request.setImage("hahaha/123.jpg");
        request.setDate(0);
        request.setAmount(0);
        request.setCategory(FUEL);
        ((Fuel) request).setType(null);
        ((Fuel) request).setLiters(0);

        assertThrows(DataConstraintException.class, () -> transactionService.update(request));

        // validation
        verify(utilsService).isFileExists(request.getImage());
        verify(transactionRepository).existsByImage(request.getImage());
        verify(utilsService).getCurrentTime();

        verify(authService).getCurrentUser();
    }

    @Test
    public void errorThrown_whenUserUpdateTransactionWithInvalidData() throws NotFoundException {
        Transaction request = new Fuel();
        request.setImage("hahaha/123.jpg");
        request.setDate(0);
        request.setAmount(0);
        request.setCategory(null);

        assertThrows(DataConstraintException.class, () -> transactionService.update(request));

        // validation
        verify(utilsService).isFileExists(request.getImage());
        verify(transactionRepository).existsByImage(request.getImage());
        verify(utilsService).getCurrentTime();

        verify(authService).getCurrentUser();
    }

    @Test
    public void errorThrown_whenImageIsAlreadyExist() throws ReimsException {

        Transaction request = new Parking();
        request.setImage("hahaha/123.jpg");
        request.setCategory(parking.getCategory());
        request.setDate(parking.getDate());
        request.setAmount(parking.getAmount());

        when(transactionRepository.existsByImage(request.getImage())).thenReturn(true);

        assertThrows(DataConstraintException.class, () -> transactionService.update(request));

        // validation
        verify(utilsService).isFileExists(request.getImage());
        verify(transactionRepository).existsByImage(request.getImage());
        verify(utilsService).getCurrentTime();

        verify(authService).getCurrentUser();
    }


}
