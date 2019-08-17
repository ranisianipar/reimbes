package com.reimbes.implementation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.reimbes.ActiveToken;
import com.reimbes.ActiveTokenRepository;
import com.reimbes.AuthService;
import com.reimbes.ReimsUser;
import com.reimbes.constant.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.reimbes.constant.SecurityConstants.*;

@Service
public class AuthServiceImpl implements AuthService {

    private static Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private String currentUsername;


    @Autowired
    private ActiveTokenRepository activeTokenRepository;

    public void setCurrentUsername(Object principal) {
        this.currentUsername = principal+"";
    }

    public String getCurrentUsername() {
        return this.currentUsername;
    }


    @Override
    public boolean isLogin(String token) {
        log.info("Check the token is in the Active Token Repo or not");
        ActiveToken activeToken = activeTokenRepository.findByToken(token);


        log.info("Active Token: "+activeToken);
        if (activeToken != null && activeToken.getExpiredTime() >= Instant.now().getEpochSecond())
            return true;

        // token expired

        log.info("Unauthenticated user try to request!");
        log.info("[DEBUG]:"+Instant.now().toEpochMilli());
        return false;
    }

    @Override
    public ActiveToken registerToken(String token) {
        log.info("Registering new token...");

        ActiveToken activeToken = activeTokenRepository.findByToken(token);
        if (activeToken == null)
            activeToken = new ActiveToken(token);

        log.info("Update token expired time.");
        activeToken.setExpiredTime(getUpdatedTime());
        return activeTokenRepository.save(activeToken);
    }

    @Override
    public void logout(HttpServletRequest req) {
        log.info("User is logging out.");
        String token = req.getHeader(HEADER_STRING);
        ActiveToken activeToken = activeTokenRepository.findByToken(token);
        activeTokenRepository.delete(activeToken);
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
                // will be useful if we provide multi-access to reimsUser
                Collection<GrantedAuthority> roles = new ArrayList();
                roles.add(new SimpleGrantedAuthority(role));
                userDetails.put("username", user);
                userDetails.put("roles",roles);
                return userDetails;
            }
        }
        return null;
    }

    @Override
    public String generateToken(UserDetails user, Collection authorities) {
        String role = authorities.iterator().next().toString();
        log.info("Generate new token. Username: "+user.getUsername()+" Role: "+role);

        String token = TOKEN_PREFIX + JWT.create()
                .withSubject(user.getUsername())
                .withClaim("expire",getUpdatedTime())
                .withClaim("role", role)
                .sign(HMAC512(SECRET.getBytes()));

        log.info("Token has been created.");
        return token;
    }

    private long getUpdatedTime() {
        return Instant.now().getEpochSecond() + SecurityConstants.TOKEN_PERIOD;
    }


}
