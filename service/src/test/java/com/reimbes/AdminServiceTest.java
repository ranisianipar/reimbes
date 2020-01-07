package com.reimbes;

import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.AdminServiceImpl;
import com.reimbes.implementation.AuthServiceImpl;
import com.reimbes.implementation.UserServiceImpl;
import com.reimbes.implementation.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.gen5.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = AdminServiceImpl.class)
public class AdminServiceTest {

    @Mock
    private Utils utils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthServiceImpl authService;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private AdminServiceImpl adminService;

    private ReimsUser user;
    private ReimsUser user2;
    private Pageable pageRequest = new PageRequest(1, 5, new Sort(Sort.Direction.DESC, "createdAt"));
    private Pageable pageForQuery = new PageRequest(0, pageRequest.getPageSize(), pageRequest.getSort());
    @Before
    public void setup() {
        user = ReimsUser.ReimsUserBuilder()
                .username("HAHA")
                .password("HEHE")
                .role(ReimsUser.Role.USER)
                .id(1)
                .gender(ReimsUser.Gender.FEMALE)
                .dateOfBirth(new Date())
                .build();


        user2 = ReimsUser.ReimsUserBuilder()
                .username(user.getUsername()+"123")
                .password("HEHE")
                .role(ReimsUser.Role.USER)
                .id(user.getId()+1)
                .gender(ReimsUser.Gender.MALE)
                .dateOfBirth(new Date())
                .build();
    }

    @Test
    public void returnAllUsers() throws ReimsException {
        List users = new ArrayList();
        users.add(user);
        Page page = new PageImpl(users);

        when(userService.getAllUsers(user.getUsername(), pageForQuery)).thenReturn(page);

        assertEquals(page, adminService.getAllUser(user.getUsername(), pageRequest));
    }

    @Test
    public void expectedError_whenPageRequestIndexIsZero() {
        assertThrows(ReimsException.class, () -> {
            adminService.getAllUser(user.getUsername(), pageForQuery);
        });
    }

    @Test
    public void returnUserById() throws ReimsException{
        when(userService.get(user.getId())).thenReturn(user);

        assertEquals(user, adminService.getUser(user.getId()));
    }


    @Test
    public void returnUser_whenAdminCreateUser() throws Exception{
        when(userService.create(user)).thenReturn(user);
        assertEquals(user, adminService.createUser(user));
    }

    @Test
    public void returnUpdatedUser_whenAdminUpdateUser() throws ReimsException {
        when(utils.getUsername()).thenReturn(user.getUsername());
        when(userService.getUserByUsername(user.getUsername())).thenReturn(user);
        when(userService.update(user2.getId(), user2)).thenReturn(user2);

        assertEquals(user2, adminService.updateUser(user2.getId(), user2, null));
    }

    @Test
    public void returnUpdatedUser_whenAdminUpdateHimself() throws ReimsException {
        when(utils.getUsername()).thenReturn(user.getUsername());
        when(userService.getUserByUsername(user.getUsername())).thenReturn(user);

        ReimsUser userWithNewData = ReimsUser.ReimsUserBuilder()
                .username(user.getUsername())
                .role(user.getRole())
                .id(user.getId())
                .build();


        userWithNewData.setId(user.getId());
        userWithNewData.setRole(user.getRole());
        userWithNewData.setUsername(user.getUsername());

        MockHttpServletResponse response = new MockHttpServletResponse();
        when(userService.updateMyData(user, response)).thenReturn(userWithNewData);

        assertEquals(userWithNewData, adminService.updateUser(user.getId(), user, response));
    }

    @Test
    public void doUserDeletion_whenAdminDeleteUserById() throws ReimsException {
        when(utils.getUsername()).thenReturn(user.getUsername());
        when(userService.getUserByUsername(user.getUsername())).thenReturn(user);
        adminService.deleteUser(user2.getId());
        verify(userService, times(1)).delete(user2.getId());
    }

    @Test
    public void throwErrorForUserDeletion_whenAdminDeleteHimself() {
        when(utils.getUsername()).thenReturn(user.getUsername());
        when(userService.getUserByUsername(user.getUsername())).thenReturn(user);
        assertThrows(ReimsException.class, () -> {
            adminService.deleteUser(user.getId());
        });

    }







}
