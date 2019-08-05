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
    public void returnUpdatedUserData_whenUpdateUser() throws ReimsException{
        when(passwordEncoder.encode(user.getPassword())).thenReturn(userWithEncodedPass.getPassword());
        when(userRepository.save(user)).thenReturn(userWithEncodedPass);

        ReimsUser newUser = userService.create(user);


    }


}
