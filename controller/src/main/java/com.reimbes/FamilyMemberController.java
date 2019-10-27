package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.FamilyMemberServiceImpl;
import com.reimbes.implementation.UserServiceImpl;
import com.reimbes.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import static com.reimbes.constant.UrlConstants.API_PREFIX;
import static com.reimbes.constant.UrlConstants.FAMILY_MEMBER_PREFIX;

@CrossOrigin(origins = UrlConstants.CROSS_ORIGIN_URL)
@RestController
@RequestMapping(API_PREFIX + FAMILY_MEMBER_PREFIX)
public class FamilyMemberController {

    @Autowired
    private UserServiceImpl familyMemberService;


    // FAMILY MEMBER THINGS
    // TANYAIN BAIKNYA GIMANA
    @PostMapping
    public BaseResponse addFamilyMember(
            @RequestParam(value = "user-id") Integer id, @RequestBody FamilyMember familyMember) {
        BaseResponse br = new BaseResponse();
        try {
            br.setData(familyMemberService.addFamilyMember(id, familyMember));
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

    @GetMapping(UrlConstants.USER_PREFIX + UrlConstants.ID_PARAM)
    public BaseResponse getAllFamilyMember(
            @PathVariable long id,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam (value = "search", defaultValue = "") String search) {
        BaseResponse br = new BaseResponse();
        Pageable pageRequest = new PageRequest(page, size, new Sort(Sort.Direction.DESC, sortBy));

        br.setData(familyMemberService.getAllFamilyMember(id, pageRequest));
        return br;
    }
}
