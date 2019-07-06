package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.implementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;


@CrossOrigin(origins = UrlConstants.CROSS_ORIGIN_URL)
@RestController
@RequestMapping(UrlConstants.API_PREFIX+UrlConstants.USER_PREFIX)
public class UserController {

    @Autowired
    private UserServiceImpl userService;


    // .xls
    @GetMapping(UrlConstants.REPORT)
    public OutputStream getReport() {
        try {
            return userService.getReport(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // update profile

    // delete account

    // get personal details




}
