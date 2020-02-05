package com.reimbes;

import com.auth0.jwt.JWT;
import com.reimbes.constant.SecurityConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.AuthServiceImpl;
import com.reimbes.interfaces.UserService;
import com.reimbes.interfaces.UtilsService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.reimbes.ReimsUser.Role.ADMIN;
import static com.reimbes.ReimsUser.Role.USER;
import static com.reimbes.constant.SecurityConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = AuthServiceImpl.class)
public class AuthServiceTest {

    private UserDetails userDetails;
    private ReimsUser user;
    private Collection authorities;


    @Mock
    private ActiveTokenRepository activeTokenRepository;

    @Mock
    private UserService userService;

    @Mock
    private UtilsService utilsService;

    @InjectMocks
    private AuthServiceImpl authService;

    @After
    public void tearDown() {
        verifyNoMoreInteractions(utilsService, userService, activeTokenRepository);
    }

    @Before
    public void setup() {

        user = ReimsUser.ReimsUserBuilder()
                .username("HAHA")
                .password("HEHE")
                .role(USER)
                .build();

        authorities = new ArrayList();
        authorities.add(user.getRole());

        userDetails = new UserDetailsImpl(user, authorities);
    }

    @Test
    public void generateTokenInJWT() {
        String result = authService.generateToken(userDetails);
        verify(utilsService).getCurrentTime();

        assertNotEquals("", result);
        assertTrue(result.contains(TOKEN_PREFIX));
    }

    @Test
    public void registerTokenAfterLogin() {
        Instant now = Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        when(utilsService.getCurrentTime()).thenReturn(now.toEpochMilli());
        String token = authService.generateToken(userDetails);
        verify(utilsService).getCurrentTime();

        ActiveToken activeToken = new ActiveToken(token); //

        authService.registerToken(token);
        verify(activeTokenRepository).findByToken(token);
        verify(utilsService, times(2)).getCurrentTime();

        activeToken.setExpiredTime(authService.getUpdatedTime());
        verify(utilsService, times(3)).getCurrentTime();

        verify(activeTokenRepository, times(1)).save(activeToken);
    }

    @Test
    public void updateExpiredTimeWheneverUserDoesSomeRequests() {
        Instant now = Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        String token = authService.generateToken(userDetails);
        verify(utilsService, times(1)).getCurrentTime();

        ActiveToken activeToken = new ActiveToken(token);
        activeToken.setExpiredTime(now.toEpochMilli() + SecurityConstants.TOKEN_PERIOD);

        when(utilsService.getCurrentTime()).thenReturn(now.toEpochMilli());
        when(activeTokenRepository.findByToken(activeToken.getToken())).thenReturn(activeToken);
        when(activeTokenRepository.save(activeToken)).thenReturn(activeToken);

        ActiveToken result = authService.registerToken(token);
        verify(utilsService, times(2)).getCurrentTime(); // 2nd calling of getCurrentTime method
        verify(activeTokenRepository).findByToken(result.getToken());
        verify(activeTokenRepository).save(result);
    }

    @Test
    public void deleteToken_whenUserDoesLogout() {
        Instant now = Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        when(utilsService.getCurrentTime()).thenReturn(now.toEpochMilli());

        String token = authService.generateToken(userDetails);
        verify(utilsService, times(1)).getCurrentTime();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_STRING, token);

        ActiveToken activeToken = new ActiveToken(token);
        activeToken.setExpiredTime(authService.getUpdatedTime());
        verify(utilsService, times(2)).getCurrentTime();

        when(activeTokenRepository.findByToken(token)).thenReturn(activeToken);
        authService.logout(request);
        verify(activeTokenRepository).findByToken(token);
        verify(activeTokenRepository, times(1)).delete(activeToken);
    }


    @Test
    public void isLoginReturnFalse_whenRequestDoesntHaveValidToken() {
        String token = "hahah";
        assertFalse(authService.isLogin(token));
        verify(activeTokenRepository).findByToken(token);
    }

    @Test
    public void isLoginReturnTrue_whenRequestHasValidToken() {
        String dummyToken = "hahahaha";
        ActiveToken token = new ActiveToken();
        token.setToken(dummyToken);
        token.setExpiredTime(authService.getUpdatedTime());
        verify(utilsService, times(1)).getCurrentTime();
        when(activeTokenRepository.findByToken(dummyToken)).thenReturn(token);

        boolean result = authService.isLogin(dummyToken);
        assertTrue(result);
        verify(utilsService, times(2)).getCurrentTime();
        verify(activeTokenRepository).findByToken(dummyToken);
    }

    @Test
    public void returnUserDetails_whenGetCurrentUserDetailMethodCalled() {
        Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        MockHttpServletRequest request = new MockHttpServletRequest();

        String token = TOKEN_PREFIX + JWT.create()
                .withSubject(user.getUsername())
                .withClaim("expire",Instant.now().getEpochSecond())
                .withClaim("role", user.getRole().toString())
                .sign(HMAC512(SECRET.getBytes()));

        request.addHeader(HEADER_STRING, token);

        HashMap result = authService.getCurrentUserDetails(request);
        assertNotNull(result);
    }
    @Test
    public void returnNull_whenGetCurrentUserDetailsWithNoToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        HashMap result = authService.getCurrentUserDetails(request);
        assertNull(result);
    }

    @Test
    public void returnReimsUserRole_byStringValue() {
        ReimsUser.Role result = authService.getRoleByString("ADMIN");
        assertEquals(ADMIN, result);

        result = authService.getRoleByString("USER");
        assertEquals(USER, result);
    }

    @Test
    public void returnCurrentUser_byFindReimsUserUsingPrincipal() throws ReimsException {
        ReimsUser user = ReimsUser.ReimsUserBuilder().username("haha").password("haha123").role(ADMIN).build();
        when(utilsService.getPrincipalUsername()).thenReturn(user.getUsername());
        when(userService.getUserByUsername(user.getUsername())).thenReturn(user);

        ReimsUser result = authService.getCurrentUser();
        verify(utilsService).getPrincipalUsername();
        verify(userService).getUserByUsername(user.getUsername());
        assertEquals(user, result);

    }

}
