package com.reimbes;

import com.reimbes.constant.UrlConstants;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = UrlConstants.BASE_URL)
@RestController
@RequestMapping(UrlConstants.ADMIN_PREFIX)
public class AdminController {

    @PostMapping(UrlConstants.ADD_USER)
    public String addUser() {
        return "Add user";
    }
}
