package com.reimbes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public interface AuthService {
    boolean isLogin(String token);
    void registerToken(String token);
    void logout(HttpServletRequest req);
    HashMap getCurrentUserDetails(HttpServletRequest req);
}
