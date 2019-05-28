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

import java.util.ArrayList;

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
            br.setData(getMapper().map(adminService.createUser(user), UserResponse.class));
        } catch (ReimsException r) {
            br.setErrors(r.getMessage());
            br.setSuccess(false);
            br.setCode(r.getCode());
            br.setStatus(r.getHttpStatus());
        }
        return br;
    }

    @GetMapping(UrlConstants.USER_PREFIX)
    public BaseResponse<ArrayList> getAllUsers(
            @RequestParam(value = "pageNumber", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "updated_at") String sortBy,
            @RequestParam (value = "search", required = false) String search) {

        return null;
    }

    private MapperFacade getMapper() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(ReimsUser.class, UserResponse.class)
                .byDefault().register();
        return mapperFactory.getMapperFacade();
    }
}
