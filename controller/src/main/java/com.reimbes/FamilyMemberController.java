package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.interfaces.FamilyMemberService;
import com.reimbes.response.BaseResponse;
import com.reimbes.response.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import static com.reimbes.constant.Mapper.*;
import static com.reimbes.constant.UrlConstants.*;

@CrossOrigin(origins = UrlConstants.CROSS_ORIGIN_URL)
@RestController
@RequestMapping(API_PREFIX + FAMILY_MEMBER_PREFIX)
public class FamilyMemberController {

    @Autowired
    private FamilyMemberService familyMemberService;

    private static Logger log = LoggerFactory.getLogger(FamilyMemberController.class);

    @GetMapping
    public BaseResponse getAll(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam (value = "search", defaultValue = "") String search) {
        BaseResponse br = new BaseResponse();
        Pageable pageRequest = new PageRequest(page, size, new Sort(Sort.Direction.DESC, sortBy));

        try {
            Page familyMembers = familyMemberService.getAll(null, search, pageRequest);
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

    @GetMapping(ID_PARAM)
    public BaseResponse getById(@PathVariable Long id) {
        BaseResponse br = new BaseResponse();

        try {
            br.setData(getFamilyMemberResponse(familyMemberService.getById(id)));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

}
