package com.reimbes;

import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.UserServiceImpl;
import com.reimbes.response.BaseResponse;
import com.reimbes.response.UserResponse;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static com.reimbes.constant.General.SPECIAL_ID;
import static com.reimbes.constant.UrlConstants.*;


@CrossOrigin(origins = CROSS_ORIGIN_URL)
@RestController
@RequestMapping(API_PREFIX + USER_PREFIX)
public class UserController {

    @Autowired
    private UserServiceImpl userService;




    // .xls
    @GetMapping(SUB_FOLDER_REPORT)
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

    // get personal details
    @GetMapping
    public BaseResponse getUser() {
        BaseResponse br = new BaseResponse();
        try {
            br.setData(getMapper().map(userService.get(SPECIAL_ID), UserResponse.class));
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    @GetMapping(FAMILY_MEMBER_PREFIX)
    public BaseResponse getAllFamilyMember(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam (value = "search", defaultValue = "") String search) {
        BaseResponse br = new BaseResponse();

        Pageable pageRequest = new PageRequest(page, size, new Sort(Sort.Direction.DESC, sortBy));
        br.setData(userService.getAllFamilyMember(null, pageRequest));
        return br;
    }

    @GetMapping(FAMILY_MEMBER_PREFIX + ID_PARAM)
    public BaseResponse getFamilyMember(@PathVariable Long id) {
        BaseResponse br = new BaseResponse();

        try {
            br.setData(userService.getFamilyMember(null, id));
        } catch (ReimsException r) {
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
