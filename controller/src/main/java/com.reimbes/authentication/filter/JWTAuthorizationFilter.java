package com.reimbes.authentication.filter;

import com.reimbes.Session;
import com.reimbes.interfaces.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static com.reimbes.constant.SecurityConstants.*;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private static Logger log = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

    @Autowired
    private AuthService authService;

    public JWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        log.info("Doing filter internal...");
        String token = req.getHeader(HEADER_STRING);

        if (token == null || !token.startsWith(TOKEN_PREFIX) || !authService.isLogin(token)) {
            chain.doFilter(req, res);
            return;
        }

        log.info("Token valid. Now, do filter internal.");
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        log.info("Get user detail by token");
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            Session activeToken = authService.getSessionByToken(token);
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(activeToken.getRole().toString()));
            if (activeToken != null) {
                return new UsernamePasswordAuthenticationToken(activeToken.getUsername(), null, authorities);
            }
            return null;
        }
        return null;
    }
}
