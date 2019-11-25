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

import static com.reimbes.constant.UrlConstants.*;

@CrossOrigin(origins = UrlConstants.CROSS_ORIGIN_URL)
@RestController
@RequestMapping(API_PREFIX + FAMILY_MEMBER_PREFIX)
public class FamilyMemberController {

    @Autowired
    private FamilyMemberServiceImpl familyMemberService;


    // FAMILY MEMBER THINGS
    // TANYAIN BAIKNYA GIMANA
    @PostMapping
    public BaseResponse create(
            @RequestParam(value = "user-id") Long id,
            @RequestBody FamilyMember familyMember) {
        BaseResponse br = new BaseResponse();
        try {
            br.setData(familyMemberService.create(id, familyMember));
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

    @GetMapping
    public BaseResponse getAll(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam (value = "search", defaultValue = "") String search) {
        BaseResponse br = new BaseResponse();
        Pageable pageRequest = new PageRequest(page, size, new Sort(Sort.Direction.DESC, sortBy));

        br.setData(familyMemberService.getAll(pageRequest));
        return br;
    }

    @GetMapping(ID_PARAM)
    public BaseResponse getById(@PathVariable Long id) {
        BaseResponse br = new BaseResponse();
        try {
            br.setData(familyMemberService.getById(id));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

    @PutMapping(ID_PARAM)
    public BaseResponse update(@PathVariable Long id, @RequestBody FamilyMember member) {
        BaseResponse br = new BaseResponse();
        br.setData(familyMemberService.update(id, member));
        return br;
    }

    // delete???



}
