package com.reimbes;

import com.reimbes.authentication.JWTAuthenticationFilter;
import com.reimbes.constant.UrlConstants;
import com.reimbes.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = UrlConstants.BASE_URL)
@RestController
@RequestMapping("/")
public class HomeController {

    @Autowired
    private UserDetailsService userService;

    @Autowired
    private JWTAuthenticationFilter authenticationFilter;

    @GetMapping(UrlConstants.LOGOUT_URL)
    public BaseResponse logout(HttpServletRequest req) {
        authenticationFilter.logout(req);
        return new BaseResponse();
    }

    @GetMapping("test")
    public String test() {
        return "ALL TEST";
    }
}
