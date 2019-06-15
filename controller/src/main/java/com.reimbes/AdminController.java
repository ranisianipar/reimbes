package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.response.BaseResponse;
import com.reimbes.response.Paging;
import com.reimbes.response.UserResponse;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@CrossOrigin(origins = UrlConstants.BASE_URL)
@RestController
@RequestMapping(UrlConstants.API_PREFIX + UrlConstants.ADMIN_PREFIX)
public class AdminController {

    @Autowired
    AdminService adminService;

    @PostMapping(UrlConstants.USER_PREFIX)
    public BaseResponse<UserResponse> createUser(@RequestBody ReimsUser user) throws Exception{
        BaseResponse<UserResponse> br = new BaseResponse<>();
        try {
            br.setData(getMapper().map(adminService.createUser(user), UserResponse.class));
        } catch (ReimsException r) {
            br.errorResponse(r);
        }
        return br;
    }

    @GetMapping(UrlConstants.USER_PREFIX)
    public BaseResponse<ArrayList> getAllUsers(
            @RequestParam(value = "pageNumber", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "updated_at") String sortBy,
            @RequestParam (value = "search", required = false) String search) {

        Pageable pageRequest = new PageRequest(page, size, new Sort(Sort.Direction.ASC, sortBy));
        Paging paging = getPagingMapper().map(pageRequest, Paging.class);
        BaseResponse br = new BaseResponse();

        br.setData(adminService.getAllUser(pageRequest));

        br.setPaging(paging);
        return br;
    }

    @GetMapping(UrlConstants.USER_PREFIX + UrlConstants.ID_PARAM)
    public BaseResponse<UserResponse> getUser(@PathVariable long id) {
        BaseResponse<UserResponse> br = new BaseResponse<>();
        try {
            br.setData(getMapper().map(adminService.getUser(id), UserResponse.class));
        } catch (ReimsException r) {
            br.errorResponse(r);
        }
        return br;
    }

    @PutMapping(UrlConstants.USER_PREFIX + UrlConstants.ID_PARAM)
    public BaseResponse<UserResponse> updateUser(@PathVariable long id, @RequestBody ReimsUser user) throws Exception{
        BaseResponse<UserResponse> br = new BaseResponse<>();
        try {
            br.setData(getMapper().map(adminService.updateUser(id,user), UserResponse.class));
        } catch (ReimsException r) {
            br.errorResponse(r);
        }
        return br;
    }


    private MapperFacade getMapper() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(ReimsUser.class, UserResponse.class)
                .byDefault().register();
        return mapperFactory.getMapperFacade();
    }

    private MapperFacade getPagingMapper() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Pageable.class, Paging.class)
                .byDefault().register();
        return mapperFactory.getMapperFacade();
    }
}
