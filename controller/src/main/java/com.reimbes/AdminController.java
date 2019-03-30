package com.reimbes;

import com.reimbes.constant.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = UrlConstants.BASE_URL)
@RestController
@RequestMapping(UrlConstants.ADMIN_PREFIX)
public class AdminController {

    @Autowired
    AdminService adminService;

    @PostMapping(UrlConstants.ADD_USER)
    public ReimsUser addUser(@RequestBody ReimsUser user) throws Exception{
        return adminService.createUser(user);
    }

    @GetMapping("/test")
    public String test() {
        return "ADMIN TEST";
    }
}
