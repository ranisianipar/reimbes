package com.reimbes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public interface AuthService {
    public void logout(HttpServletRequest req);
    public HashMap getCurrentUserDetails(HttpServletRequest req);
}
