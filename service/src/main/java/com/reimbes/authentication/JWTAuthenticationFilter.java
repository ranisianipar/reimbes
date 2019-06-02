package com.reimbes.authentication;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reimbes.ReimsUser;
import com.reimbes.implementation.AuthServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.reimbes.constant.SecurityConstants.*;

@Component
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static Logger log = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Autowired
    private AuthServiceImpl authService;


    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }


    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    /*
    * where we parse the user's credentials and issue them to the AuthenticationManager
    *
    * */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        log.info("Attempting authentication");
        try {
            ReimsUser creds = new ObjectMapper()
                    .readValue(req.getInputStream(), ReimsUser.class);

            log.info("USERNAME: "+creds.getUsername()+" PASS: "+creds.getPassword());
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * where we parse the user's credentials and issue them to the AuthenticationManager which is the method called when
     * a user successfully logs in. We use this method to generate a JWT for this user
     * */
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException{

        UserDetails user = (UserDetails) auth.getPrincipal();
        Collection authorities = user.getAuthorities();
        String role = authorities.iterator().next().toString();

        String token = TOKEN_PREFIX + JWT.create()
                .withSubject(user.getUsername())
                .withClaim("role", role)
                .sign(HMAC512(SECRET.getBytes()));
        res.addHeader(HEADER_STRING,token);

        authService.registerToken(token);
    }
}
