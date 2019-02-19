package com.reimbes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = Constant.BASE_URL)
@RestController
@RequestMapping(Constant.ROOT_URL)
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping(Constant.LOGIN_URL)
    public void login(@RequestParam String username, @RequestParam String password) {

        return;
    }

    @GetMapping(Constant.LOGOUT_URL)
    public void logout() {

    }

}
