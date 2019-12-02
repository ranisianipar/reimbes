package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.FamilyMemberServiceImpl;
import com.reimbes.response.BaseResponse;
import com.reimbes.response.FamilyMemberResponse;
import com.reimbes.response.FuelResponse;
import com.reimbes.response.Paging;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.reimbes.constant.UrlConstants.*;

@CrossOrigin(origins = UrlConstants.CROSS_ORIGIN_URL)
@RestController
@RequestMapping(API_PREFIX + FAMILY_MEMBER_PREFIX)
public class FamilyMemberController {

    @Autowired
    private FamilyMemberServiceImpl familyMemberService;

    private static Logger log = LoggerFactory.getLogger(FamilyMemberController.class);


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
            @RequestParam (value = "search", defaultValue = "") String search,
            @RequestParam (value = "user-id", defaultValue = "0") String userId) {
        BaseResponse br = new BaseResponse();
        Pageable pageRequest = new PageRequest(page, size, new Sort(Sort.Direction.DESC, sortBy));

        try {
            Page familyMembers = familyMemberService.getAll(new Long(userId), search, pageRequest);
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

    @PutMapping(ID_PARAM)
    public BaseResponse update(
            @PathVariable Long id,
            @RequestParam(value = "user-id") Long userId,
            @RequestBody FamilyMember familyMember) {
        BaseResponse br = new BaseResponse();
        try {
            br.setData(getFamilyMemberResponse(familyMemberService.update(id, familyMember, userId)));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    // delete???


    private MapperFacade getPagingMapper() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Pageable.class, Paging.class)
                .byDefault().register();
        return mapperFactory.getMapperFacade();
    }

    private FamilyMemberResponse getFamilyMemberResponse(FamilyMember familyMember) {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        mapperFactory.classMap(FamilyMember.class, FamilyMemberResponse.class)
                .field("familyMemberOf.id", "claimBy")
                .byDefault().register();

        mapperFactory.getMapperFacade().map(familyMember, FamilyMemberResponse.class);


        return mapperFactory.getMapperFacade()
                .map(familyMember, FamilyMemberResponse.class);

    }

    private List<? extends FamilyMemberResponse> getAllFamilyMemberResponses(List<FamilyMember> familyMembers) {
        log.info("Mapping object to web response...");
        List<FamilyMemberResponse> familyMemberResponses = new ArrayList<>();
        Iterator<FamilyMember> iterator = familyMembers.iterator();
        FamilyMember familyMember;
        while (iterator.hasNext()) {
            familyMember = iterator.next();
            familyMemberResponses.add(getFamilyMemberResponse(familyMember));
        }

        return familyMemberResponses;
    }

}
