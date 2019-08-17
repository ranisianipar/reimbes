package com.reimbes;

import com.reimbes.constant.UrlConstants;
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


@CrossOrigin(origins = UrlConstants.CROSS_ORIGIN_URL)
@RestController
@RequestMapping(UrlConstants.API_PREFIX+UrlConstants.USER_PREFIX)
public class UserController {

    @Autowired
    private UserServiceImpl userService;


    // .xls
    @GetMapping(UrlConstants.REPORT)
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
    public BaseResponse updateUser(@RequestBody ReimsUser user) {
        BaseResponse br = new BaseResponse();
        try {
            br.setData(userService.updateMyData(user));
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }


        return br;
    }

    // get personal details
    @GetMapping
    public BaseResponse getUser() {
        BaseResponse br = new BaseResponse();
        try {
            br.setData(getMapper().map(userService.get(1), UserResponse.class));
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
