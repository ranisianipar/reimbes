package com.reimbes.authentication;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reimbes.ActiveToken;
import com.reimbes.ActiveTokenRepository;
import com.reimbes.ReimsUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.reimbes.constant.SecurityConstants.*;

/*
* SOURCE: https://stackoverflow.com/questions/19896870/why-is-my-spring-autowired-field-null
*
* */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

//    @Autowired
//    private ActiveTokenRepository activeTokenRepository;

    private AuthenticationManager authenticationManager;

    @Autowired
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    /*
    * where we parse the user's credentials and issue them to the AuthenticationManager
    *
    * */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            ReimsUser creds = new ObjectMapper()
                    .readValue(req.getInputStream(), ReimsUser.class);

            return authenticationManager.authenticate(
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

        String token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withClaim("role", role)
                .sign(HMAC512(SECRET.getBytes()));
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);

//        activeTokenRepository.save(new ActiveToken(token));
    }
}
