package com.reimbes.interfaces;

import com.reimbes.Session;
import com.reimbes.ReimsUser;
import com.reimbes.exception.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public interface AuthService {
    boolean isLogin(String token);
    long getUpdatedTime();
    HashMap getCurrentUserDetails(HttpServletRequest req);
    ReimsUser getCurrentUser() throws NotFoundException;
    ReimsUser.Role getRoleByString(String roleString);
    Session getSessionByToken(String token);
    Session registerOrUpdateSession(Session session);
    String generateOrGetToken(UserDetails user);
    void logout(HttpServletRequest req);
}
