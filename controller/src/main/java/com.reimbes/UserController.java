package com.reimbes;

import com.reimbes.implementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = Constant.BASE_URL)
@RestController
@RequestMapping(Constant.USER_PREFIX)
public class UserController {

    @Autowired
    UserServiceImpl userService;

    // .xls
    @GetMapping(Constant.MONTHLY_REPORT)
    public void getMonthlyReport() {
        return;
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws Exception{
        return userService.create(user);
    }

    @DeleteMapping("/all")
    public void deleteUsers() {
        userService.deleteAll().toString();
    }


    @GetMapping("/all")
    public List<User> getAllUser() {
        return userService.getAllUsers();
    }

    @GetMapping("/random")
    public User getRandomUser() {
        return userService.getRandomUser();
    }

}
