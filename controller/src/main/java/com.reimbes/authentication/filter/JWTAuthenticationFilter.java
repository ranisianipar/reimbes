package com.reimbes.authentication.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.reimbes.ReimsUser;
import com.reimbes.Session;
import com.reimbes.UserDetailsImpl;
import com.reimbes.interfaces.AuthService;
import com.reimbes.request.LoginRequest;
import com.reimbes.response.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static com.reimbes.constant.SecurityConstants.HEADER_STRING;

@Component
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static Logger log = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Autowired
    private AuthService authService;


    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }


    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    /*
     * where we parse the reimsUser's credentials and issue them to the AuthenticationManager
     *
     * */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        log.info("Attempting authentication");
        try {
            LoginRequest creds = new ObjectMapper()
                    .readValue(req.getInputStream(), LoginRequest.class);

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
     * where we parse the reimsUser's credentials and issue them to the AuthenticationManager which is the method called when
     * a reimsUser successfully logs in. We use this method to generate a JWT for this reimsUser
     * */
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException {

        log.info("Authentication succeed!");
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        Collection authorities = user.getAuthorities();
        String token = authService.generateOrGetToken(user);
        ReimsUser.Role role = authService.getRoleByString(authorities.iterator().next().toString());

        // retrieve informative response for frontend needs
        res.setHeader(HEADER_STRING, token);
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(user.getUsername());
        userResponse.setId(user.getUserId());
        userResponse.setRole(role);

        // Creating Object of ObjectMapper define in Jackson Api
        ObjectMapper objectMapper = new ObjectMapper();

        String userJsonString = objectMapper.writeValueAsString(userResponse);

        PrintWriter out = res.getWriter();
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        out.print(userJsonString);
        out.flush();

        Session session = Session.builder().token(token).username(user.getUsername()).role(role).build();
        authService.registerOrUpdateSession(session);
    }
}
