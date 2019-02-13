package com.reimbes;

import com.reimbes.request.LoginRequest;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = Constant.BASE_URL)
@RestController
//@RequestMapping(Constant.USER_PREFIX)
public class UserController {


    @PostMapping(Constant.LOGIN_URL)
    public void login(@RequestParam String username, @RequestParam String password) {

        return;
    }

    // .xls
    @GetMapping(Constant.MONTHLY_REPORT)
    public void getMonthlyReport() {
        return;
    }

    @PostMapping(Constant.ADD_USER)
    public void addUser() {
        return;
    }
}
