package com.reimbes;

import com.auth0.jwt.JWT;
import com.reimbes.constant.SecurityConstants;
import com.reimbes.implementation.AuthServiceImpl;
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

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.reimbes.ReimsUser.Role.USER;
import static com.reimbes.constant.SecurityConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = AuthServiceImpl.class)
public class AuthServiceTest {

    private UserDetails userDetails;
    private ReimsUser user = new ReimsUser();
    private Collection authorities;


    @Mock
    private ActiveTokenRepository activeTokenRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @Before
    public void setup() {
        user.setUsername("HAHA");
        user.setPassword("HEHE");
        user.setRole(USER);

        authorities = new ArrayList();
        authorities.add(user.getRole());

        userDetails = new UserDetailsImpl(user, authorities);
    }

    @Test
    public void generateTokenInJWT() {
        String token = authService.generateToken(userDetails, authorities);
        assertNotEquals("", token);
        assertTrue(token.contains(TOKEN_PREFIX));
    }

    @Test
    public void registerTokenAfterLogin() {
        Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        String token = authService.generateToken(userDetails, authorities);
        ActiveToken activeToken = new ActiveToken(token);
        authService.registerToken(token);
        activeToken.setExpiredTime(Instant.now().getEpochSecond() + SecurityConstants.TOKEN_PERIOD);
        verify(activeTokenRepository, times(1)).save(activeToken);
    }

    @Test
    public void updateExpiredTimeWheneverUserDoesSomeRequests() {
        Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        String token = authService.generateToken(userDetails, authorities);
        ActiveToken activeToken = new ActiveToken(token);
        activeToken.setExpiredTime(Instant.now().getEpochSecond() + SecurityConstants.TOKEN_PERIOD);

        when(activeTokenRepository.findByToken(token)).thenReturn(activeToken);
        authService.registerToken(token);
        activeToken.setExpiredTime(activeToken.getExpiredTime() + SecurityConstants.TOKEN_PERIOD);
        verify(activeTokenRepository, times(1)).save(activeToken);
    }

    @Test
    public void deleteToken_whenUserDoesLogout() {
        Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        String token = authService.generateToken(userDetails, authorities);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_STRING, token);

        ActiveToken activeToken = new ActiveToken(token);
        activeToken.setExpiredTime(Instant.now().getEpochSecond() + SecurityConstants.TOKEN_PERIOD);
        when(activeTokenRepository.findByToken(token)).thenReturn(activeToken);
        authService.logout(request);
        verify(activeTokenRepository, times(1)).delete(activeToken);
    }


    @Test
    public void isLoginReturnFalse_whenRequestDoesntHaveValidToken() {
        assertFalse(authService.isLogin("hahah"));
    }

    @Test
    public void isLoginReturnTrue_whenRequestHasValidToken() {
        String dummyToken = "hahahaha";
        ActiveToken token = new ActiveToken();
        token.setToken(dummyToken);
        token.setExpiredTime(Instant.now().getEpochSecond()+TOKEN_PERIOD);
        when(activeTokenRepository.findByToken(dummyToken)).thenReturn(token);
        assertTrue(authService.isLogin(dummyToken));
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

        assertNotNull(authService.getCurrentUserDetails(request));
    }
    @Test
    public void returnNull_whenGetCurrentUserDetailsWithNoToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertNull(authService.getCurrentUserDetails(request));
    }

}
