package com.reimbes;

import com.reimbes.constant.SecurityConstants;
import com.reimbes.constant.UrlConstants;
import com.reimbes.implementation.AuthServiceImpl;
import com.reimbes.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = UrlConstants.BASE_URL)
@RestController
@RequestMapping(UrlConstants.API_PREFIX)
public class HomeController {

    @Autowired
    private AuthServiceImpl authService;

    @PostMapping(UrlConstants.LOGIN_URL)
    public BaseResponse login(HttpServletRequest req) {
        return new BaseResponse();
    }

    @GetMapping(UrlConstants.LOGOUT_URL)
    public BaseResponse logout(HttpServletRequest req) {
        authService.logout(req);
        return new BaseResponse();
    }

    @GetMapping(UrlConstants.ISLOGIN_URL)
    public BaseResponse isLogin(HttpServletRequest req) {
        String token = req.getHeader(SecurityConstants.HEADER_STRING);
        BaseResponse br = new BaseResponse();

        br.setData("logged in");
        if (!authService.isLogin(token))
            br.setData("haven't logged in yet");
        return br;
    }
}
