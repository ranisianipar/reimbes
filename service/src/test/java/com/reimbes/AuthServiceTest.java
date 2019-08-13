package com.reimbes;

import com.auth0.jwt.JWT;
import com.reimbes.constant.SecurityConstants;
import com.reimbes.implementation.AuthServiceImpl;
import com.reimbes.implementation.UserDetailsImpl;
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
        activeToken.setExpiredTime(Instant.now().toEpochMilli() + SecurityConstants.TOKEN_PERIOD);
        authService.registerToken(token);
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
    public void returnDetailsOfAuthenticatedUser() {
        Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        String token = authService.generateToken(userDetails, authorities);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_STRING, token);

        ActiveToken activeToken = new ActiveToken(token);
        activeToken.setExpiredTime(Instant.now().getEpochSecond() + SecurityConstants.TOKEN_PERIOD);

        assertNotNull(authService.getCurrentUserDetails(request));
    }

    @Test
    public void returnNoUserDetails_whenRequestDoesntHaveToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertNull(authService.getCurrentUserDetails(request));
    }

    @Test
    public void returnNoUserDetails_whenTokenInvalid() {
        Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        String token = TOKEN_PREFIX + JWT.create()
                .withSubject(null)
                .withClaim("expire",Instant.now().toEpochMilli())
                .withClaim("role", "USER")
                .sign(HMAC512(SECRET.getBytes()));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_STRING, token);

        ActiveToken activeToken = new ActiveToken(token);
        activeToken.setExpiredTime(Instant.now().getEpochSecond() + SecurityConstants.TOKEN_PERIOD);

        assertNull(authService.getCurrentUserDetails(request));
    }

    @Test
    public void returnTrue_whenIsLoginCalledByAuthenticatedUser() {
        Instant.now(Clock.fixed(
                Instant.parse("2018-08-22T10:00:00Z"),
                ZoneOffset.UTC));

        String token = authService.generateToken(userDetails, authorities);
        ActiveToken activeToken = new ActiveToken(token);
        activeToken.setExpiredTime(Instant.now().toEpochMilli() + SecurityConstants.TOKEN_PERIOD);

        when(activeTokenRepository.findByToken(activeToken.getToken())).thenReturn(activeToken);

        assertTrue(authService.isLogin(activeToken.getToken()));
    }

    @Test
    public void returnFalse_whenIsLoginCalledByUnauthorizedUserThatHasUnregisteredToken() {
        String token = authService.generateToken(userDetails, authorities);

        assertFalse(authService.isLogin(token));
    }

    @Test
    public void setCurrentUserNameTest() {
        assertNull(authService.getCurrentUsername());
        authService.setCurrentUsername("hahahaha");
        assertNotNull(authService.getCurrentUsername());
    }

}
