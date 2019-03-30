package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.implementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = UrlConstants.BASE_URL)
@RestController
@RequestMapping(UrlConstants.USER_PREFIX)
public class UserController {

    @Autowired
    private UserServiceImpl userService;


    // .xls
    @GetMapping(UrlConstants.MONTHLY_REPORT)
    public String getMonthlyReport() {
        return "Get monthly report";
    }

    @GetMapping("/test")
    public String test() {
        return "USER TEST";
    }


}
