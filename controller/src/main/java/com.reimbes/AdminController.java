package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.AdminServiceImpl;
import com.reimbes.response.*;
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

import static com.reimbes.constant.Mapper.*;
import static com.reimbes.constant.UrlConstants.*;

@CrossOrigin(origins = UrlConstants.CROSS_ORIGIN_URL)
@RestController
@RequestMapping(UrlConstants.API_PREFIX + UrlConstants.ADMIN_PREFIX)
public class AdminController {
    private static Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminServiceImpl adminService;

    @PostMapping(UrlConstants.USER_PREFIX)
    public BaseResponse<UserResponse> createUser(@RequestBody ReimsUser user) {
        log.info("[POST] Create user.");

        BaseResponse<UserResponse> br = new BaseResponse<>();
        try {
            br.setData(getUserResponseMapper().map(adminService.createUser(user), UserResponse.class));
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

        log.info(String.format("[GET] Get all users with criteria page: %d, size: %d, sortBy: %s, search: %s", page, size, sortBy, search ));
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
        log.info(String.format("[GET] Get user with ID: %d", id));
        BaseResponse<UserResponse> br = new BaseResponse<>();
        try {
            br.setData(getUserResponseMapper().map(adminService.getUser(id), UserResponse.class));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

    @PutMapping(UrlConstants.USER_PREFIX + UrlConstants.ID_PARAM)
    public BaseResponse<UserResponse> updateUser(@PathVariable long id, @RequestBody ReimsUser user, HttpServletResponse response){
        log.info(String.format("[PUT] Update user with ID: %d", id));

        BaseResponse<UserResponse> br = new BaseResponse<>();
        try {
            br.setData(getUserResponseMapper().map(adminService.updateUser(id, user, response), UserResponse.class));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

    @DeleteMapping(UrlConstants.USER_PREFIX + UrlConstants.ID_PARAM)
    public BaseResponse<UserResponse> deleteUser(@PathVariable long id) throws ReimsException {
        log.info(String.format("[DELETE] Delete user with ID: %d", id));
        BaseResponse<UserResponse> br = new BaseResponse<>();
        adminService.deleteUser(id);

        return br;
    }

//    MEDICAL
    @GetMapping(UrlConstants.MEDICAL_PREFIX)
    public BaseResponse<Medical> getAllMedical(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "date") String sortBy,
            @RequestParam(value = "start", defaultValue = "0") String start,
            @RequestParam(value = "end", defaultValue = "0") String end,
            @RequestParam(value = "user-id", required = false) String userId,
            @RequestParam (value = "search", defaultValue = "") String search
    ) {
        log.info(String.format("[GET] Get all medicals with criteria page: %d, size: %d, sortBy: %s, search: %s, time range: %d-%d",
                page, size, sortBy, search, start, end));
        BaseResponse br = new BaseResponse();
        Pageable pageRequest = new PageRequest(page, size, new Sort(Sort.Direction.ASC, sortBy));


        try {
            Page medicals = adminService.getAllMedical(pageRequest, search, new Long(start), new Long(end), new Long(userId));
            Paging paging = getPagingMapper().map(pageRequest, Paging.class);
            br.setData(getAllMedicalResponse(
                    medicals.getContent()
            ));
            paging.setTotalPages(medicals.getTotalPages());
            paging.setTotalRecords(medicals.getContent().size());
            br.setPaging(paging);
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    @GetMapping(MEDICAL_PREFIX + ID_PARAM)
    public BaseResponse get(@PathVariable long id) {
        log.info(String.format("[GET] Get Medical with ID: %d", id));
        BaseResponse br = new BaseResponse();
        try {
            Medical result = adminService.getMedical(id);
            br.setData(getMedicalMapper(result).map(result, MedicalWebModel.class));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }


//    FAMILY MEMBER
    @PostMapping(FAMILY_MEMBER_PREFIX)
    public BaseResponse<FamilyMemberResponse> addFamilyMember(
            @RequestParam(value = "user-id", defaultValue = "0") String id,
            @RequestBody FamilyMember familyMember
    ) {
        log.info(String.format("[POST] Create Family Member for User with ID: %s", id));
        BaseResponse br = new BaseResponse();
        try {
            br.setData(adminService.createFamilyMember(new Long(id), familyMember));
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

    @PutMapping(FAMILY_MEMBER_PREFIX + ID_PARAM)
    public BaseResponse update(
            @PathVariable Long id,
            @RequestParam(value = "user-id", defaultValue = "0") String userId,
            @RequestBody FamilyMember familyMember) {
        log.info(String.format("[PUT] Update Family Member with ID: %s", id));
        BaseResponse br = new BaseResponse();
        try {
            br.setData(getFamilyMemberResponse(adminService.updateFamilyMember(id, familyMember, new Long(userId))));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    @GetMapping(FAMILY_MEMBER_PREFIX)
    public BaseResponse<FamilyMemberResponse> getAllFamilyMember(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam (value = "search", defaultValue = "") String search,
            @RequestParam (value = "user-id", required = false) Long userId
    ) {
        log.info(String.format("[GET] Get all family members with criteria page: %d, size: %d, sortBy: %s, search: %s",
                page, size, sortBy, search ));
        BaseResponse br = new BaseResponse();
        Pageable pageRequest = new PageRequest(page, size, new Sort(Sort.Direction.DESC, sortBy));

        try {
            Page familyMembers = adminService.getAllFamilyMember(userId, search, pageRequest);
            Paging paging = getPagingMapper().map(pageRequest, Paging.class);
            paging.setTotalPages(familyMembers.getTotalPages());
            paging.setTotalRecords(familyMembers.getContent().size());

            br.setData(getAllFamilyMemberResponses(familyMembers.getContent()));
            br.setPaging(paging);
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    @GetMapping(FAMILY_MEMBER_PREFIX + ID_PARAM)
    public BaseResponse<FamilyMemberResponse> getFamilyMember(@PathVariable long id) {
        log.info(String.format("[GET] Get Family Member with ID: %d", id));
        BaseResponse br = new BaseResponse();
        try {
            br.setData(getFamilyMemberResponse(adminService.getFamilyMember(id)));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

    @DeleteMapping(FAMILY_MEMBER_PREFIX + ID_PARAM)
    public BaseResponse deleteFamilyMember(@PathVariable long id) {
        log.info(String.format("[DELETE] Delete Family Member with ID: %d", id));
        BaseResponse br = new BaseResponse();
        try {
            adminService.deleteFamilyMember(id);
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }
}
