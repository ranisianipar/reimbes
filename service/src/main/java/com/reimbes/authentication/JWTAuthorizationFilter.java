package com.reimbes.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.reimbes.ActiveToken;
import com.reimbes.ActiveTokenRepository;
import com.reimbes.implementation.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Service;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import static com.reimbes.constant.SecurityConstants.*;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    @Autowired
    private ActiveTokenRepository activeTokenRepository;

    @Autowired
    private AuthServiceImpl authService;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String token = req.getHeader(HEADER_STRING);

//        !activeTokenRepository.existsByToken(token)
        if (token == null || !token.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        HashMap userDetails = authService.getCurrentUserDetails(req);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails.get("username"),
                        null, (Collection<? extends GrantedAuthority>) userDetails.get("roles"));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }
}
