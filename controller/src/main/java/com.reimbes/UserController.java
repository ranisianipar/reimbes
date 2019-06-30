package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.implementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = UrlConstants.CROSS_ORIGIN_URL)
@RestController
@RequestMapping(UrlConstants.API_PREFIX+UrlConstants.USER_PREFIX)
public class UserController {

    @Autowired
    private UserServiceImpl userService;


    // .xls
    @GetMapping(UrlConstants.MONTHLY_REPORT)
    public String getMonthlyReport() {
        return "Get monthly report";
    }


}
