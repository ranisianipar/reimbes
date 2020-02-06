package com.reimbes;

import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.*;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import static com.reimbes.constant.General.PARKING_VALUE;
import static com.reimbes.constant.SecurityConstants.HEADER_STRING;
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
    public void errorThrown_AfterCreateReimsUserWithEmptyPassword() throws ReimsException {
        user.setPassword("");
        assertThrows(DataConstraintException.class, () -> userService.create(user));
    }

    @Test
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
    public void returnUpdatedUserData_whenAdminUpdateUser() throws ReimsException{
        ReimsUser oldUser = user;
        ReimsUser newUser = ReimsUser.ReimsUserBuilder()
                .id(oldUser.getId())
                .password(oldUser.getPassword())
                .role(oldUser.getRole()).build();

        when(passwordEncoder.encode(user.getPassword())).thenReturn(userWithEncodedPass.getPassword());
        when(userRepository.save(user)).thenReturn(userWithEncodedPass);
        when(userRepository.findOne(user.getId())).thenReturn(user);

        newUser.setUsername(oldUser.getUsername() + "123"); // update data

        when(userRepository.findByUsername(oldUser.getUsername())).thenReturn(oldUser);
        when(userRepository.save(newUser)).thenReturn(newUser);

        ReimsUser result = userService.update(oldUser.getId(), newUser);
        verify(userRepository).findOne(oldUser.getId());

        // validation
        verify(userRepository).findByUsername(oldUser.getUsername());

        verify(passwordEncoder).encode(newUser.getPassword());
        verify(utilsService).getCurrentTime();
        verify(userRepository).save(newUser);
        assertEquals(newUser, result);
    }

    @Test
    public void returnUpdatedUserData_whenUserUpdateTheirOwnData() throws ReimsException {
        when(passwordEncoder.encode(user.getPassword())).thenReturn(userWithEncodedPass.getPassword());
        when(userRepository.save(user)).thenReturn(userWithEncodedPass);
        ReimsUser newUser = userService.create(user);

        when(utilsService.getPrincipalUsername()).thenReturn(newUser.getUsername());
        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(newUser);

        String oldUsername = newUser.getUsername();
        newUser.setUsername(newUser.getUsername()+"123");

        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(newUser);
        when(userRepository.save(newUser)).thenReturn(newUser);

        newUser = userService.update(1, newUser);

        assertNotEquals(newUser.getUsername(), oldUsername);
        assertNotNull(newUser.getUpdatedAt());
    }

    @Test
    public void expectedErrorThrown_whenUserUpdateDataWithSomeNullFieldsData() throws ReimsException {

        when(passwordEncoder.encode(user.getPassword())).thenReturn(userWithEncodedPass.getPassword());
        when(userRepository.save(user)).thenReturn(userWithEncodedPass);

        ReimsUser newUser = userService.create(user);

        when(userRepository.findOne(newUser.getId())).thenReturn(newUser);

        newUser.setUsername(null);
        newUser.setPassword("");

        assertThrows(ReimsException.class, () -> {
            userService.update(newUser.getId(), newUser);
        });
    }

    @Test
    public void extepctedErrorThrown_whenUserInputInsecurePassAndDuplicateUsername() throws ReimsException{
        when(passwordEncoder.encode(user.getPassword())).thenReturn(userWithEncodedPass.getPassword());
        when(userRepository.save(user)).thenReturn(userWithEncodedPass);

        ReimsUser newUser = userService.create(user);

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
    }

    @Test
    public void returnUsernameExistance(){
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);
        assertTrue(userService.isExist(user.getUsername()));
        assertFalse(userService.isExist("Haha"));
    }

    @Test
    public void removeUnregisteredUser(){
        userService.delete(100);
        verify(transactionService, times(0)).deleteTransactionImageByUser(user);
        verify(userRepository, times(0)).delete(user);
    }

    @Test
    public void removeRegisteredUser() {
        when(userRepository.findOne(user.getId())).thenReturn(user);

        userService.delete(user.getId());
        verify(transactionService, times(1)).deleteTransactionImageByUser(user);
        verify(userRepository, times(1)).delete(user);

    }

    @Test
    public void returnReimsUserByUsername() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        assertNotNull(userService.getUserByUsername(user.getUsername()));
    }

    @Test
    public void returnUserById() throws ReimsException{
        when(userRepository.findOne(user.getId())).thenReturn(user);
        assertEquals(user,userService.get(user.getId()));

    }

    @Test
    public void returnCurrentUserData() throws ReimsException{
        when(utilsService.getPrincipalUsername()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        assertEquals(user,userService.get(1));

    }

    @Test
    public void errorThrown_whenNoUserFoundWithTheRequestedId() {
        assertThrows(ReimsException.class, () -> {
            userService.get(123);
        });
    }

    @Test
    public void returnAllUsers() {
        Pageable pageable = new PageRequest(1, 5, new Sort(Sort.Direction.DESC, "createdAt"));
        List users = new ArrayList();
        users.add(user);
        Page page = new PageImpl(users);
        when(userRepository.findByIdGreaterThanAndUsernameContainingIgnoreCase(1, user.getUsername(), pageable)).thenReturn(page);

        assertEquals(page, userService.getAllUsers(user.getUsername(), pageable));
    }

    @Test
    public void makingAReport_whenUserAskedForIt() throws Exception {
        String fakeReport = "webfw";
        when(reportGeneratorService.getReport(user,new Long(0),new Long(0), PARKING_VALUE)).thenReturn(fakeReport);
        when(authService.getCurrentUser()).thenReturn(user);

        assertEquals(userService.getReport(new Long(0), new Long(0), PARKING_VALUE), fakeReport);
    }

    @Test
    public void updatePersonalData() throws ReimsException {
        when(passwordEncoder.encode(user.getPassword())).thenReturn(userWithEncodedPass.getPassword());
        when(userRepository.save(user)).thenReturn(userWithEncodedPass);
        when(utilsService.getPrincipalUsername()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        String dummyToken = "123";

        // update token with latest username
        Collection authorities =  new ArrayList();

        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));

        // add token
        when(authService.generateToken(new UserDetailsImpl(user, authorities))).thenReturn(dummyToken);

        MockHttpServletResponse response = new MockHttpServletResponse();
        ReimsUser reimsUser = userService.updateMyData(user, response);

        assertEquals(dummyToken, response.getHeader(HEADER_STRING));
        assertEquals(user.getUsername(), reimsUser.getUsername());
        assertEquals(user.getRole(), reimsUser.getRole());
    }

    @Test
    public void errorThrown_whenGetImageByInvalidImagePathFormat() throws Exception {
        byte[] fakeImage = new byte[100];
        String fakeImagepath = "hahaha/x123.png";
        when(authService.getCurrentUser()).thenReturn(user);
        when(utilsService.getFile(fakeImagepath)).thenReturn(fakeImage);
        assertThrows(NotFoundException.class, () -> userService.getImage(fakeImagepath));
    }

    @Test
    public void errorThrown_whenGetUnexistImage() throws Exception {
        String fakeImagepath = String.format("/%d/%s", user.getId(), "xoxo.jpg");
        when(authService.getCurrentUser()).thenReturn(user);
        when(utilsService.getFile(fakeImagepath)).thenThrow(new IOException());
        assertThrows(NotFoundException.class, () -> userService.getImage(fakeImagepath));
    }

    @Test
    public void returnImage_whenGetImageByValidImagePathFormat() throws Exception {
        byte[] expectedImage = new byte[10];
        String expectedResult = Base64.getEncoder().encodeToString(expectedImage);
        String fakeImagepath = String.format("/%d/%s", user.getId(), "haha.jpg");
        when(authService.getCurrentUser()).thenReturn(user);
        when(utilsService.getFile(fakeImagepath)).thenReturn(expectedImage);
        assertEquals(expectedResult, userService.getImage(fakeImagepath));
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
