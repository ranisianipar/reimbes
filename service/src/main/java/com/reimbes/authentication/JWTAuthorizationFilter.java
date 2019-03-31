package com.reimbes.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.reimbes.implementation.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static com.reimbes.constant.SecurityConstants.*;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String token = req.getHeader(HEADER_STRING);

        if (token == null || !token.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }
        // di cek di redis --> KALO LOGOUT, HAPUS DI REDIS
        // if (token isNotExist in redis)
        // yg ditaro di-redis id user

        HashMap userDetails = getCurrentUserDetails(req);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails.get("username"),
                        null, (Collection<? extends GrantedAuthority>) userDetails.get("roles"));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("CHAIN =>"+chain);
        chain.doFilter(req, res);
    }

    public static HashMap getCurrentUserDetails(HttpServletRequest request) {
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
}
