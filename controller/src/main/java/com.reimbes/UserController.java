package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.UserServiceImpl;
import com.reimbes.response.BaseResponse;
import com.reimbes.response.Paging;
import com.reimbes.response.UserResponse;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
    public BaseResponse getReport(
            @RequestParam(value = "start", defaultValue = "0") long start,
            @RequestParam(value = "end", defaultValue = "0") long end
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
            br.setData(getMapper().map(userService.update(0, user), UserResponse.class));
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
            br.setData(getMapper().map(userService.get(0), UserResponse.class));
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
