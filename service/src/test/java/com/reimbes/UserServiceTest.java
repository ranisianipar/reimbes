package com.reimbes;

import com.reimbes.constant.SecurityConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.AuthServiceImpl;
import com.reimbes.implementation.ReportGeneratorServiceImpl;
import com.reimbes.implementation.TransactionServiceImpl;
import com.reimbes.implementation.UserServiceImpl;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    private ReportGeneratorServiceImpl reportGeneratorService;

    @Mock
    private AuthServiceImpl authService;

    @Mock
    private TransactionServiceImpl transactionService;

    @Mock
    private ReimsUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private List<ReimsUser> users = new ArrayList<>();

    private ReimsUser user;

    private ReimsUser userWithEncodedPass;

    @Before
    public void setup() {
        user = new ReimsUser();
        user.setUsername("@min");
        user.setRole(ReimsUser.Role.ADMIN);
        user.setPassword("1234567890");
        user.setId(2);
        users.add(user);

        userWithEncodedPass = new ReimsUser();
        userWithEncodedPass.setUsername(user.getUsername());
        userWithEncodedPass.setRole(user.getRole());
        userWithEncodedPass.setId(user.getId());
        userWithEncodedPass.setPassword("123456xxx");
    }

    @Test
    public void returnUserAfterCreateReimsUserWithValidDataAndEncodedPassword() throws ReimsException {
        when(passwordEncoder.encode(user.getPassword())).thenReturn(userWithEncodedPass.getPassword());
        when(userRepository.save(user)).thenReturn(userWithEncodedPass);

        ReimsUser newUser = userService.create(user);
        assertNotNull(newUser);
        assertEquals(userWithEncodedPass.getPassword(), newUser.getPassword());
    }

    @Test
    public void returnUpdatedUserData_whenAdminUpdateUser() throws ReimsException{
        String dummyToken = "123xyz";
        Collection authorities =  new ArrayList();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));

        when(passwordEncoder.encode(user.getPassword())).thenReturn(userWithEncodedPass.getPassword());
        when(userRepository.save(user)).thenReturn(userWithEncodedPass);

        ReimsUser newUser = userService.create(user);

        when(userRepository.findOne(newUser.getId())).thenReturn(newUser);

        String oldUsername = newUser.getUsername();
        newUser.setUsername(newUser.getUsername()+"123");

        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(newUser);
        when(userRepository.save(newUser)).thenReturn(newUser);
        when(authService.generateToken(new UserDetailsImpl(newUser, authorities), authorities)).thenReturn(dummyToken);

        newUser = userService.update(newUser.getId(), newUser, new MockHttpServletResponse());

        assertNotEquals(newUser.getUsername(), oldUsername);
        assertNotNull(newUser.getUpdatedAt());
    }

    @Test
    public void returnUpdatedUserData_whenUserUpdateTheirOwnData() throws ReimsException {
        String dummyToken = "123xyz";
        Collection authorities =  new ArrayList();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));


        when(passwordEncoder.encode(user.getPassword())).thenReturn(userWithEncodedPass.getPassword());
        when(userRepository.save(user)).thenReturn(userWithEncodedPass);
        ReimsUser newUser = userService.create(user);

        when(authService.getCurrentUsername()).thenReturn(newUser.getUsername());
        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(newUser);

        String oldUsername = newUser.getUsername();
        newUser.setUsername(newUser.getUsername()+"123");

        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(newUser);
        when(userRepository.save(newUser)).thenReturn(newUser);

        when(authService.generateToken(new UserDetailsImpl(newUser, authorities), authorities)).thenReturn(dummyToken);

        MockHttpServletResponse response = new MockHttpServletResponse();
        response.addHeader(SecurityConstants.HEADER_STRING, "");

        newUser = userService.update(1, newUser, response);

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
        newUser.setPassword(null);


        assertThrows(ReimsException.class, () -> {
            userService.update(newUser.getId(), newUser, new MockHttpServletResponse());
        });
    }

    @Test
    public void extepctedErrorThrown_whenUserInputInsecurePassAndDuplicateUsername() throws ReimsException{
        when(passwordEncoder.encode(user.getPassword())).thenReturn(userWithEncodedPass.getPassword());
        when(userRepository.save(user)).thenReturn(userWithEncodedPass);

        ReimsUser newUser = userService.create(user);

        ReimsUser user2 = new ReimsUser();
        user2.setUsername("REIMBES");
        user2.setPassword("xxxxx");
        user2.setRole(ReimsUser.Role.USER);

        ReimsUser user2WithEncodedPass = new ReimsUser();
        user2.setUsername("REIMBES");
        user2.setPassword("xxxxx123");
        user2.setRole(ReimsUser.Role.USER);

        when(passwordEncoder.encode(user.getPassword())).thenReturn(user2WithEncodedPass.getPassword());
        when(userRepository.save(user2)).thenReturn(user2WithEncodedPass);

        when(userRepository.findOne(newUser.getId())).thenReturn(userWithEncodedPass);
        when(userRepository.findByUsername(user2.getUsername())).thenReturn(user2WithEncodedPass);

        newUser.setUsername("REIMBES");
        newUser.setPassword("REIMBES");


        assertThrows(ReimsException.class, () -> {
            userService.update(newUser.getId(), newUser, new MockHttpServletResponse());
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
        userService.deleteUser(100);
        verify(transactionService, times(0)).deleteByUser(user);
        verify(userRepository, times(0)).delete(user);
    }

    @Test
    public void removeRegisteredUser() {
        when(userRepository.findOne(user.getId())).thenReturn(user);

        userService.deleteUser(user.getId());
        verify(transactionService, times(1)).deleteByUser(user);
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
        when(authService.getCurrentUsername()).thenReturn(user.getUsername());
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
        byte[] fakeReport = new byte[100];
        when(reportGeneratorService.getReport(user,new Long(0),new Long(0))).thenReturn(fakeReport);
        when(authService.getCurrentUsername()).thenReturn(user.getUsername());
        when(userService.getUserByUsername(user.getUsername())).thenReturn(user);

        assertEquals(userService.getReport("0", "0"), fakeReport);
    }

    @Test
    public void successfullyLoadUserDetailsByUsername() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        assertNotNull(userService.loadUserByUsername(user.getUsername()));
    }

    @Test
    public void throwNotFoundError_whenUnregisteredUsernameTryToLoadUserDetails() {
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(user.getUsername());
        });
    }


}
