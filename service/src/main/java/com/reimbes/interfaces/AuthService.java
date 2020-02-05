package com.reimbes.interfaces;

import com.reimbes.ActiveToken;
import com.reimbes.ReimsUser;
import com.reimbes.exception.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;

public interface AuthService {
    String generateToken(UserDetails user);
    boolean isLogin(String token);
    ActiveToken registerToken(String token);
    void logout(HttpServletRequest req);
    HashMap getCurrentUserDetails(HttpServletRequest req);
    ReimsUser getCurrentUser() throws NotFoundException;
    ReimsUser.Role getRoleByString(String roleString); // helper method to determine role by string
    long getUpdatedTime();
}
