package com.reimbes;

import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.UserServiceImpl;
import com.reimbes.interfaces.AuthService;
import com.reimbes.interfaces.ReportGeneratorService;
import com.reimbes.interfaces.TransactionService;
import com.reimbes.interfaces.UtilsService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.reimbes.constant.General.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;
import static org.junit.gen5.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = UserServiceImpl.class)
public class UserServiceTest {

    @Mock
    private ReportGeneratorService reportGeneratorService;

    @Mock
    private UtilsService utilsService;

    @Mock
    private AuthService authService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private ReimsUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private List<ReimsUser> users = new ArrayList<>();

    private ReimsUser user;

    private ReimsUser userWithEncodedPass;

    @After
    public void tearDown() {
        verifyNoMoreInteractions(utilsService, authService, transactionService, userRepository, passwordEncoder);
    }

    @Before
    public void setup() {
        user = ReimsUser.ReimsUserBuilder()
                .username("@min")
                .role(ReimsUser.Role.ADMIN)
                .password("1234567890")
                .id(2)
                .build();

        users.add(user);

        userWithEncodedPass = ReimsUser.ReimsUserBuilder()
                .username(user.getUsername())
                .role(user.getRole())
                .password(user.getPassword())
                .id(user.getId())
                .build();
    }

    @Test
    public void returnUserAfterCreateReimsUserWithValidDataAndEncodedPassword() throws ReimsException {
        when(passwordEncoder.encode(user.getPassword())).thenReturn(userWithEncodedPass.getPassword());
        when(userRepository.save(user)).thenReturn(userWithEncodedPass);

        ReimsUser result = userService.create(user);
        verify(utilsService).getCurrentTime();
        verify(passwordEncoder).encode(user.getPassword());

        // validation
        verify(userRepository).findByUsername(user.getUsername());

        verify(userRepository).save(user);

        assertNotNull(result);
        assertEquals(userWithEncodedPass.getPassword(), result.getPassword());
        assertEquals(userWithEncodedPass, result);
    }

    @Test
    public void errorThrown_AfterCreateReimsUserWithEmptyPassword() {
        user.setPassword("");
        assertThrows(DataConstraintException.class, () -> userService.create(user));
    }


    //@Test
    public void returnUserAfterCreateReimsUserWithDuplicateUsername() {
        when(passwordEncoder.encode(user.getPassword())).thenReturn(userWithEncodedPass.getPassword());
        when(userRepository.save(user)).thenReturn(userWithEncodedPass);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        assertThrows(ReimsException.class, () -> {
            userService.create(user);
        });

        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    public void returnUpdatedUserData_whenAdminUpdateUser() throws ReimsException {
        ReimsUser oldUser = user;
        ReimsUser newUser = ReimsUser.ReimsUserBuilder()
                .id(oldUser.getId())
                .password(oldUser.getPassword())
                .role(oldUser.getRole()).build();

        when(userRepository.save(user)).thenReturn(userWithEncodedPass);
        when(userRepository.findOne(user.getId())).thenReturn(user);

        newUser.setUsername(oldUser.getUsername() + "123"); // update data

        when(userRepository.findByUsername(oldUser.getUsername())).thenReturn(oldUser);
        when(userRepository.save(newUser)).thenReturn(newUser);

        ReimsUser result = userService.update(oldUser.getId(), newUser);
        verify(userRepository).findOne(oldUser.getId());

        // validation
        verify(userRepository).findByUsername(oldUser.getUsername());

        verify(utilsService).getCurrentTime();
        verify(userRepository).save(newUser);
        assertEquals(newUser, result);
    }

    @Test
    public void returnUpdatedUserData_whenUserUpdateTheirOwnData() throws ReimsException { ;
        String token = "241k2jb4";
        Session session = Session.builder()
                .token(token)
                .build();
        when(authService.getCurrentUser()).thenReturn(user);

        ReimsUser newUser = ReimsUser.ReimsUserBuilder()
                .username(user.getUsername() + "123")
                .role(user.getRole())
                .password(user.getPassword())
                .id(2)
                .build();

        when(userRepository.save(newUser)).thenReturn(newUser);

        ReimsUser result = userService.updateMyData(user, token);
        verify(authService).getCurrentUser();
        verify(userRepository).findByUsername(newUser.getUsername());
        verify(utilsService).getCurrentTime();
        verify(userRepository).save(newUser);
        verify(authService).registerOrUpdateSession(session);
        assertEquals(result, user);
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    public void expectedErrorThrown_whenUserUpdateDataWithSomeNullFieldsData() {
        when(passwordEncoder.encode(user.getPassword())).thenReturn(userWithEncodedPass.getPassword());
        when(userRepository.save(user)).thenReturn(userWithEncodedPass);

        ReimsUser newUser = user;

        when(userRepository.findOne(newUser.getId())).thenReturn(newUser);

        newUser.setUsername(null);
        newUser.setPassword("");

        assertThrows(ReimsException.class, () -> {
            userService.update(newUser.getId(), newUser);
        });

        verify(userRepository).findOne(newUser.getId());
    }

    @Test
    public void extepctedErrorThrown_whenUserInputInsecurePassAndDuplicateUsername() throws ReimsException {
        when(passwordEncoder.encode(user.getPassword())).thenReturn(userWithEncodedPass.getPassword());
        when(userRepository.save(user)).thenReturn(userWithEncodedPass);

        ReimsUser newUser = user;

        ReimsUser user2 = ReimsUser.ReimsUserBuilder()
                .username("REIMBES")
                .password("xxxxxx")
                .role(ReimsUser.Role.USER)
                .build();

        ReimsUser user2WithEncodedPass = ReimsUser.ReimsUserBuilder()
                .username(user2.getUsername())
                .password("xxxxx123")
                .role(ReimsUser.Role.USER)
                .build();

        when(passwordEncoder.encode(user.getPassword())).thenReturn(user2WithEncodedPass.getPassword());
        when(userRepository.save(user2)).thenReturn(user2WithEncodedPass);

        when(userRepository.findOne(newUser.getId())).thenReturn(userWithEncodedPass);
        when(userRepository.findByUsername(user2.getUsername())).thenReturn(user2WithEncodedPass);

        newUser.setUsername("REIMBES");
        newUser.setPassword("REIMBES");


        assertThrows(ReimsException.class, () -> {
            userService.update(newUser.getId(), newUser);
        });

        verify(userRepository).findOne(newUser.getId());
        verify(userRepository).findByUsername(newUser.getUsername());
    }

    @Test
    public void returnTrueForExistUsername() {
        boolean result;
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);
        result = userService.isExist(user.getUsername());
        verify(userRepository).existsByUsername(user.getUsername());
        assertTrue(result);
    }

    @Test
    public void returnFalseForUnexistUsername() {
        String unexistUsername = "haha";
        boolean result;
        result = userService.isExist(unexistUsername);

        verify(userRepository).existsByUsername(unexistUsername);
        assertFalse(result);
    }

    @Test
    public void removeUnregisteredUser() {
        long randomUserId = 100;
        boolean result = userService.delete(randomUserId);
        verify(userRepository).findOne(randomUserId);
        assertFalse(result);
    }

    @Test
    public void removeRegisteredUser() {
        when(userRepository.findOne(user.getId())).thenReturn(user);
        boolean result = userService.delete(user.getId());
        verify(userRepository).findOne(user.getId());
        verify(transactionService, times(1)).deleteTransactionImageByUser(user);
        verify(userRepository, times(1)).delete(user);
        assertTrue(result);

    }

    @Test
    public void returnReimsUserByUsername() {
        ReimsUser expectedResult = user;
        when(userRepository.findByUsername(expectedResult.getUsername())).thenReturn(expectedResult);
        ReimsUser result = userService.getUserByUsername(expectedResult.getUsername());
        verify(userRepository).findByUsername(expectedResult.getUsername());
        assertEquals(expectedResult, result);
    }

    @Test
    public void returnUserById() throws ReimsException {
        ReimsUser expectedResult = user;
        when(userRepository.findOne(expectedResult.getId())).thenReturn(expectedResult);

        ReimsUser result = userService.get(expectedResult.getId());
        verify(userRepository).findOne(expectedResult.getId());
        assertEquals(expectedResult, result);

    }

    @Test
    public void returnCurrentUserData() throws ReimsException {
        ReimsUser currentUser = user;
        when(utilsService.getPrincipalUsername()).thenReturn(currentUser.getUsername());
        when(userRepository.findByUsername(currentUser.getUsername())).thenReturn(currentUser);

        ReimsUser result = userService.get(IDENTITY_CODE);
        verify(utilsService).getPrincipalUsername();
        verify(userRepository).findByUsername(currentUser.getUsername());
        assertEquals(currentUser, result);

    }

    @Test
    public void errorThrown_whenNoUserFoundWithTheRequestedId() {
        long userId = 123;
        assertThrows(ReimsException.class, () -> {
            userService.get(userId);
        });
        verify(userRepository).findOne(userId);
    }

    @Test
    public void returnAllUsers() {
        Pageable pageable = new PageRequest(1, 5, new Sort(Sort.Direction.DESC, "createdAt"));
        List users = new ArrayList();
        users.add(user);
        Page expectedResult = new PageImpl(users);
        when(userRepository.findByIdGreaterThanAndUsernameContainingIgnoreCase(IDENTITY_CODE, user.getUsername(), pageable)).thenReturn(expectedResult);

        Page result = userService.getAllUsers(user.getUsername(), pageable);
        verify(userRepository).findByIdGreaterThanAndUsernameContainingIgnoreCase(IDENTITY_CODE, user.getUsername(), pageable);
        assertEquals(expectedResult, result);
    }

    @Test
    public void makingAReport_whenUserAskedForIt() throws Exception {
        String fakeReport = "webfw";
        when(reportGeneratorService.getReport(user, DEFAULT_LONG_VALUE, DEFAULT_LONG_VALUE, PARKING_VALUE)).thenReturn(fakeReport);
        when(authService.getCurrentUser()).thenReturn(user);

        String result = userService.getReport(DEFAULT_LONG_VALUE, DEFAULT_LONG_VALUE, PARKING_VALUE);
        verify(authService).getCurrentUser();
        verify(reportGeneratorService).getReport(user, DEFAULT_LONG_VALUE, DEFAULT_LONG_VALUE, PARKING_VALUE);
        assertEquals(fakeReport, result);
    }


    @Test
    public void errorThrown_whenGetImageByInvalidImagePathFormat() throws Exception {
        String fakeImagepath = "hahaha/x123.png";
        when(authService.getCurrentUser()).thenReturn(user);
        assertThrows(NotFoundException.class, () -> userService.getImage(fakeImagepath));
        verify(authService).getCurrentUser();
    }

    @Test
    public void errorThrown_whenGetUnexistImage() throws Exception {
        String fakeImagepath = String.format("/%d/%s", user.getId(), "xoxo.jpg");
        when(authService.getCurrentUser()).thenReturn(user);
        when(utilsService.getFile(fakeImagepath)).thenThrow(new IOException());
        assertThrows(NotFoundException.class, () -> userService.getImage(fakeImagepath));
        verify(authService).getCurrentUser();
        verify(utilsService).getFile(fakeImagepath);
    }

    @Test
    public void returnImage_whenGetImageByValidImagePathFormat() throws Exception {
        byte[] expectedImage = new byte[10];
        String expectedResult = Base64.getEncoder().encodeToString(expectedImage);
        String fakeImagepath = String.format("/%d/%s", user.getId(), "haha.jpg");
        when(authService.getCurrentUser()).thenReturn(user);
        when(utilsService.getFile(fakeImagepath)).thenReturn(expectedImage);

        String result = userService.getImage(fakeImagepath);
        verify(authService).getCurrentUser();
        verify(utilsService).getFile(fakeImagepath);
        assertEquals(expectedResult, result);
    }

    @Test
    public void returnTrue_whenChangePasswordSucceed() throws NotFoundException {
        ReimsUser user = ReimsUser.ReimsUserBuilder().username("user 1").password("xoxoxox").build();
        String newPassword = user.getPassword() + "123";
        String passwordAfterEncoding = newPassword + "8913ihgvq";
        ReimsUser userWithLatestData = ReimsUser.ReimsUserBuilder()
                .username(user.getUsername())
                .password(passwordAfterEncoding)
                .build();

        when(authService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.encode(newPassword)).thenReturn(passwordAfterEncoding);

        boolean result = userService.changePassword(newPassword);
        verify(authService).getCurrentUser();
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(userWithLatestData);
        assertTrue(result);

    }



}
