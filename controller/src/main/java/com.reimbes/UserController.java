package com.reimbes;

import com.reimbes.constant.UrlConstants;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = UrlConstants.BASE_URL)
@RestController
@RequestMapping(UrlConstants.USER_PREFIX)
public class UserController {

    @GetMapping("/{id}")
    public String getUser(@PathVariable String id) {
        return "GET USER ID: "+id;
    }

    // .xls
    @GetMapping(UrlConstants.MONTHLY_REPORT)
    public void getMonthlyReport() {
        return;
    }

    @PostMapping(UrlConstants.ADD_USER)
    public void addUser() {
        return;
    }
}
