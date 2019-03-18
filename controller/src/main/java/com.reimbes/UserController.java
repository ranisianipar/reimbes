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
    UserServiceImpl userService;


    // .xls
    @GetMapping(UrlConstants.MONTHLY_REPORT)
    public void getMonthlyReport() {
        return;
    }


}
