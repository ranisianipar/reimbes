package com.reimbes;

import com.reimbes.constant.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = UrlConstants.BASE_URL)
@RestController
@RequestMapping("/")
public class HomeController {
    @Autowired
    private UserDetailsService userService;


    @GetMapping(UrlConstants.LOGOUT_URL)
    public String logout() {
        return "LOGOUT";
    }

    @GetMapping("test")
    public String test() {
        return "ALL TEST";
    }
}
