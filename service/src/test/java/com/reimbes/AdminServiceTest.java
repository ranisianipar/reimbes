package com.reimbes;

import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.AdminServiceImpl;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.gen5.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = AdminServiceImpl.class)
public class AdminServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private AdminServiceImpl adminService;

    private ReimsUser user = new ReimsUser();
    private Pageable pageRequest = new PageRequest(1, 5, new Sort(Sort.Direction.DESC, "createdAt"));
    private Pageable pageForQuery = new PageRequest(0, pageRequest.getPageSize(), pageRequest.getSort());
    @Before
    public void setup() {
        user.setUsername("HAHA");
        user.setPassword("HEHE");
        user.setRole(ReimsUser.Role.USER);
        user.setId(1);
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
        ReimsUser user2 = new ReimsUser();
        user2.setId(user.getId());
        user2.setRole(ReimsUser.Role.ADMIN);
        user2.setPassword(user.getPassword());
        user2.setUsername(user.getUsername());
        when(userService.update(user.getId(), user2, new MockHttpServletResponse())).thenReturn(user2);

        assertEquals(user2, adminService.updateUser(user.getId(), user2));
    }

    @Test
    public void doUserDeletion_whenAdminDeleteUserById() {
        adminService.deleteUser(user.getId());
        verify(userService, times(1)).deleteUser(user.getId());
    }







}
