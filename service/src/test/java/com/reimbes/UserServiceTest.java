package com.reimbes;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNotEquals;
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
        user.setId(1);
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
        when(passwordEncoder.encode(user.getPassword())).thenReturn(userWithEncodedPass.getPassword());
        when(userRepository.save(user)).thenReturn(userWithEncodedPass);

        ReimsUser newUser = userService.create(user);

        when(userRepository.findOne(newUser.getId())).thenReturn(newUser);

        String oldUsername = newUser.getUsername();
        newUser.setUsername(newUser.getUsername()+"123");

        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(newUser);
        when(userRepository.save(newUser)).thenReturn(newUser);

        newUser = userService.update(newUser.getId(), newUser);

        assertNotEquals(newUser.getUsername(), oldUsername);
        assertNotNull(newUser.getUpdatedAt());
    }

    @Test
    public void returnUpdatedUserData_whenUserUpdateTheirOwnData() throws ReimsException {
        when(passwordEncoder.encode(user.getPassword())).thenReturn(userWithEncodedPass.getPassword());
        when(userRepository.save(user)).thenReturn(userWithEncodedPass);
        ReimsUser newUser = userService.create(user);

        when(authService.getCurrentUsername()).thenReturn(newUser.getUsername());
        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(newUser);

        String oldUsername = newUser.getUsername();
        newUser.setUsername(newUser.getUsername()+"123");

        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(newUser);
        when(userRepository.save(newUser)).thenReturn(newUser);

        newUser = userService.update(0, newUser);

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
            userService.update(newUser.getId(), newUser);
        });
    }


}
