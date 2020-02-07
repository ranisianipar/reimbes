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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private Collection<GrantedAuthority> authorities;


    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserService userService;

    @Mock
    private UtilsService utilsService;

    @InjectMocks
    private AuthServiceImpl authService;

    @After
    public void tearDown() {
        verifyNoMoreInteractions(utilsService, userService, sessionRepository);
    }

    @Before
    public void setup() {
        user = ReimsUser.ReimsUserBuilder()
                .username("HAHA")
                .password("HEHE")
                .role(USER)
                .build();
        authorities = new ArrayList();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
        userDetails = new UserDetailsImpl(user, authorities);
    }

    @Test
    public void generateTokenInJWT() {
        String result = authService.generateOrGetToken(userDetails);
        verify(sessionRepository).findByUsername(userDetails.getUsername());
        verify(utilsService).getCurrentTime();

        assertNotEquals("", result);
        assertTrue(result.contains(TOKEN_PREFIX));
    }

    @Test
    public void updateExpirationDate_whenTheAccountHasBeenLoggedIn() {
        Instant now = Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        Session session = Session.builder()
                .token("agwehliw")
                .username(userDetails.getUsername())
                .expiredTime(now.toEpochMilli() + SecurityConstants.TOKEN_PERIOD)
                .build();

        when(utilsService.getCurrentTime()).thenReturn(now.toEpochMilli());
        when(sessionRepository.findByUsername(userDetails.getUsername())).thenReturn(session);
        when(sessionRepository.findByToken(session.getToken())).thenReturn(session);
        when(sessionRepository.save(session)).thenReturn(session);

        String result = authService.generateOrGetToken(userDetails);

        verify(sessionRepository).findByUsername(userDetails.getUsername());
        verify(sessionRepository).findByToken(session.getToken());
        verify(sessionRepository).existsByUsername(session.getUsername());
        verify(utilsService).getCurrentTime();
        verify(sessionRepository).save(session);

        assertEquals(session.getToken(), result);
    }

    @Test
    public void registerTokenAfterLogin() {
        Instant now = Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        String token = "jwbeflkbawf";

        Session session = Session.builder()
                .token(token)
                .username("ksgf")
                .expiredTime(now.toEpochMilli() + SecurityConstants.TOKEN_PERIOD)
                .build();

        when(utilsService.getCurrentTime()).thenReturn(now.toEpochMilli());
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = authService.registerOrUpdateSession(session);
        verify(sessionRepository).findByToken(token);
        verify(sessionRepository).existsByUsername(session.getUsername());
        verify(utilsService).getCurrentTime();
        verify(sessionRepository).save(session);

        assertEquals(session.getToken(), result.getToken());
    }

    @Test
    public void updateExpiredTimeWheneverUserDoesSomeRequests() {
        Instant now = Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        String token = "kuawgeflwigef";

        Session session = Session.builder().token(token).build();
        session.setExpiredTime(now.toEpochMilli() + SecurityConstants.TOKEN_PERIOD);

        when(utilsService.getCurrentTime()).thenReturn(now.toEpochMilli());
        when(sessionRepository.findByToken(session.getToken())).thenReturn(session);
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = authService.registerOrUpdateSession(session);
        verify(sessionRepository).findByToken(result.getToken());
        verify(sessionRepository).existsByUsername(session.getUsername());
        verify(utilsService).getCurrentTime();
        verify(sessionRepository).save(result);
    }

    @Test
    public void deleteToken_whenUserDoesLogout() {
        Instant now = Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        when(utilsService.getCurrentTime()).thenReturn(now.toEpochMilli());

        String token = "kuawgeflwigef";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_STRING, token);

        Session session = Session.builder().token(token).build();
        session.setExpiredTime(authService.getUpdatedTime());
        verify(utilsService).getCurrentTime();

        when(sessionRepository.findByToken(token)).thenReturn(session);
        authService.logout(request);
        verify(sessionRepository).findByToken(token);
        verify(sessionRepository, times(1)).delete(session);
    }


    @Test
    public void isLoginReturnFalse_whenRequestDoesntHaveValidToken() {
        String token = "hahah";
        assertFalse(authService.isLogin(token));
        verify(sessionRepository).findByToken(token);
    }

    @Test
    public void isLoginReturnTrue_whenRequestHasValidToken() {
        String dummyToken = "hahahaha";
        Session token = Session.builder()
                .token(dummyToken)
                .expiredTime(authService.getUpdatedTime())
                .build();
        verify(utilsService, times(1)).getCurrentTime();
        when(sessionRepository.findByToken(dummyToken)).thenReturn(token);

        boolean result = authService.isLogin(dummyToken);
        assertTrue(result);
        verify(utilsService, times(2)).getCurrentTime();
        verify(sessionRepository).findByToken(dummyToken);
    }

    @Test
    public void returnUserDetails_whenGetCurrentUserDetailMethodCalled() {
        Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        MockHttpServletRequest request = new MockHttpServletRequest();

        String token = TOKEN_PREFIX + JWT.create()
                .withSubject(user.getUsername())
                .withClaim("expire", Instant.now().getEpochSecond())
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
