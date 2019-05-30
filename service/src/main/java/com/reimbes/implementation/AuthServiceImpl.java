package com.reimbes.implementation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reimbes.ActiveToken;
import com.reimbes.ActiveTokenRepository;
import com.reimbes.AuthService;
import com.reimbes.ReimsUser;
import com.reimbes.constant.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static com.reimbes.constant.SecurityConstants.*;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private ActiveTokenRepository activeTokenRepository;

    @Override
    public boolean isLogin(String token) {
        ActiveToken activeToken = activeTokenRepository.findByToken(token);
        if (activeToken != null && activeToken.getExpiredTime() > Instant.now().getEpochSecond()) return true;

        // token expired
        return false;
    }

    @Override
    public void registerToken(String token) {
        ActiveToken activeToken = activeTokenRepository.findByToken(token);
        if (activeToken == null)
            activeToken = new ActiveToken(token);
        // update the time of token
        activeToken.setExpiredTime(Instant.now().getEpochSecond() + SecurityConstants.TOKEN_PERIOD);
        activeTokenRepository.save(activeToken);
    }

    @Override
    public void logout(HttpServletRequest req) {
        String token = req.getHeader(HEADER_STRING);
        activeTokenRepository.delete(new ActiveToken(token));
    }

    @Override
    public HashMap getCurrentUserDetails(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""));
            String user = decodedJWT.getSubject();

            String role = decodedJWT.getClaim("role").asString();

            if (user != null) {
                HashMap<String, Object> userDetails = new HashMap<>();
                // will be useful if we provide multi-access to user
                Collection<GrantedAuthority> roles = new ArrayList();
                roles.add(new SimpleGrantedAuthority(role));
                userDetails.put("username", user);
                userDetails.put("roles",roles);
                return userDetails;
            }
        }
        return null;
    }

    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            ReimsUser creds = new ObjectMapper()
                    .readValue(req.getInputStream(), ReimsUser.class);
            return null;
//            return getAuthenticationManager().authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            creds.getUsername(),
//                            creds.getPassword(),
//                            new ArrayList<>())
//            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
