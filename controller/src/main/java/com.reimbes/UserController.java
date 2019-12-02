package com.reimbes;

import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.UserServiceImpl;
import com.reimbes.response.BaseResponse;
import com.reimbes.response.UserResponse;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static com.reimbes.constant.General.IDENTITY_CODE;
import static com.reimbes.constant.UrlConstants.*;


@CrossOrigin(origins = CROSS_ORIGIN_URL)
@RestController
@RequestMapping(API_PREFIX + USER_PREFIX)
public class UserController {

    @Autowired
    private UserServiceImpl userService;




    // .xls
    @GetMapping(REPORT_PREFIX)
    public BaseResponse getReport(
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end
    ) {
        try {
            userService.getReport(start, end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BaseResponse();
    }

    // update profile
    @PutMapping
    public BaseResponse updateUser(@RequestBody ReimsUser user, HttpServletResponse response) {
        BaseResponse br = new BaseResponse();
        try {
            br.setData(userService.updateMyData(user, response));
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }


        return br;
    }

    @GetMapping
    public BaseResponse getUser() {
        BaseResponse br = new BaseResponse();
        try {
            br.setData(getMapper().map(userService.get(IDENTITY_CODE), UserResponse.class));
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }


    private MapperFacade getMapper() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(ReimsUser.class, UserResponse.class)
                .byDefault().register();
        return mapperFactory.getMapperFacade();
    }




}
