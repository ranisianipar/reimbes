package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.AdminServiceImpl;
import com.reimbes.response.BaseResponse;
import com.reimbes.response.Paging;
import com.reimbes.response.UserResponse;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.reimbes.constant.UrlConstants.FAMILY_MEMBER_PREFIX;

@CrossOrigin(origins = UrlConstants.CROSS_ORIGIN_URL)
@RestController
@RequestMapping(UrlConstants.API_PREFIX + UrlConstants.ADMIN_PREFIX)
public class AdminController {

    private static Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminServiceImpl adminService;

    @PostMapping(UrlConstants.USER_PREFIX)
    public BaseResponse<UserResponse> createUser(@RequestBody ReimsUser user) throws Exception{
        BaseResponse<UserResponse> br = new BaseResponse<>();
        try {
            br.setData(getMapper().map(adminService.createUser(user), UserResponse.class));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

    @GetMapping(UrlConstants.USER_PREFIX)
    public BaseResponse<ArrayList> getAllUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam (value = "search", defaultValue = "") String search) {

        Pageable pageRequest = new PageRequest(page, size, new Sort(Sort.Direction.DESC, sortBy));
        BaseResponse br = new BaseResponse();

        try {
            Page users = adminService.getAllUser(search, pageRequest);

            br.setData(getAllUserResponses(users.getContent()));

            Paging paging = getPagingMapper().map(pageRequest, Paging.class);

            paging.setTotalPages(users.getTotalPages());
            paging.setTotalRecords(users.getContent().size());
            br.setPaging(paging);
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    @GetMapping(UrlConstants.USER_PREFIX + UrlConstants.ID_PARAM)
    public BaseResponse<UserResponse> getUser(@PathVariable long id) {
        BaseResponse<UserResponse> br = new BaseResponse<>();
        try {
            br.setData(getMapper().map(adminService.getUser(id), UserResponse.class));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

    @PutMapping(UrlConstants.USER_PREFIX + UrlConstants.ID_PARAM)
    public BaseResponse<UserResponse> updateUser(@PathVariable long id, @RequestBody ReimsUser user, HttpServletResponse response) throws Exception{
        BaseResponse<UserResponse> br = new BaseResponse<>();
        try {
            br.setData(getMapper().map(adminService.updateUser(id,user, response), UserResponse.class));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

    @DeleteMapping(UrlConstants.USER_PREFIX + UrlConstants.ID_PARAM)
    public BaseResponse<UserResponse> deleteUser(@PathVariable long id) throws ReimsException {
        BaseResponse<UserResponse> br = new BaseResponse<>();
        adminService.deleteUser(id);

        return br;
    }

    @PostMapping(UrlConstants.USER_PREFIX + UrlConstants.ID_PARAM + FAMILY_MEMBER_PREFIX)
    public BaseResponse addFamilyMember(@PathVariable long id, @RequestBody FamilyMember familyMember) {
        BaseResponse br = new BaseResponse();
        br.setData(adminService.addMember(id, familyMember));
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

    private List<UserResponse> getAllUserResponses(List<ReimsUser> users) {
        List<UserResponse> userResponses = new ArrayList<>();
        Iterator iterator = users.iterator();
        while (iterator.hasNext()) {
            userResponses.add(getMapper().map(iterator.next(), UserResponse.class));
        }
        return userResponses;
    }
}
