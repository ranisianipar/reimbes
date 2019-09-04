package com.reimbes;

import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;

public interface AuthService {
    String generateToken(UserDetails user, Collection authorities);
    boolean isLogin(String token);
    ActiveToken registerToken(String token);
    void logout(HttpServletRequest req);
    HashMap getCurrentUserDetails(HttpServletRequest req);
}
