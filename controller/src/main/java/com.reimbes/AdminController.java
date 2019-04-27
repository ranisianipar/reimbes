package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.response.BaseResponse;
import com.reimbes.response.UserResponse;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = UrlConstants.BASE_URL)
@RestController
@RequestMapping(UrlConstants.API_PREFIX+UrlConstants.ADMIN_PREFIX)
public class AdminController {

    @Autowired
    AdminService adminService;

    @PostMapping(UrlConstants.ADD_USER)
    public BaseResponse<UserResponse> addUser(@RequestBody ReimsUser user) throws Exception{
        BaseResponse<UserResponse> br = new BaseResponse<>();
        try {
            br.setValue(getMapper().map(adminService.createUser(user), UserResponse.class));
        } catch (ReimsException r) {
            br.setErrorMessage(r.getMessage());
            br.setSuccess(false);
            br.setCode(r.getCode());
            br.setErrorCode(r.getHttpStatus());
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
