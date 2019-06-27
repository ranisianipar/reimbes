package com.reimbes.authentication.filter;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.reimbes.ReimsUser;
import com.reimbes.constant.SecurityConstants;
import com.reimbes.implementation.AuthServiceImpl;
import com.reimbes.implementation.UserDetailsImpl;
import com.reimbes.response.UserResponse;
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
import java.io.PrintWriter;
import java.time.Instant;
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

        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        Collection authorities = user.getAuthorities();
        String token = authService.generateToken(user,authorities);

//      Modify Login Response
        res.addHeader(HEADER_STRING,token);

        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(user.getUsername());
        userResponse.setId(user.getUserId());


        if (authorities.iterator().next().toString().equals("USER"))
            userResponse.setRole(ReimsUser.Role.USER);
        else userResponse.setRole(ReimsUser.Role.ADMIN);

        String userJsonString = new Gson().toJson(userResponse);
        PrintWriter out = res.getWriter();
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        out.print(userJsonString);
        out.flush();

        authService.registerToken(token);
    }
}
