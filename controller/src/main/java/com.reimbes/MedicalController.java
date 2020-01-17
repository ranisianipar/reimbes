package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.MedicalServiceImpl;
import com.reimbes.interfaces.MedicalService;
import com.reimbes.response.*;
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

import static com.reimbes.constant.General.NULL_USER_ID_CODE;
import static com.reimbes.constant.Mapper.*;
import static com.reimbes.constant.UrlConstants.IMAGE_PARAM;
import static com.reimbes.constant.UrlConstants.IMAGE_PREFIX;

@CrossOrigin(origins = UrlConstants.CROSS_ORIGIN_URL)
@RestController
@RequestMapping(UrlConstants.API_PREFIX + UrlConstants.MEDICAL_PREFIX)
public class MedicalController {
    private static Logger log = LoggerFactory.getLogger(MedicalController.class);

    @Autowired
    private MedicalService medicalService;

    @GetMapping
    public BaseResponse getAll(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "date") String sortBy,
            @RequestParam(value = "start", defaultValue = "0") String start,
            @RequestParam(value = "end", defaultValue = "0") String end,
            @RequestParam (value = "search", defaultValue = "") String search
    ) {
        BaseResponse br = new BaseResponse();
        Pageable pageRequest = new PageRequest(page, size, new Sort(Sort.Direction.ASC, sortBy));

         try {
             log.info("Get All Medicals");
             Page medicals = medicalService.getAll(pageRequest, search, new Long(start), new Long(end), NULL_USER_ID_CODE);
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

    @GetMapping(UrlConstants.ID_PARAM)
    public BaseResponse get(@PathVariable long id) {
        BaseResponse br = new BaseResponse();
        try {
            log.info("Get Medical by ID:" + id);
            Medical result = medicalService.get(id);
            br.setData(getMedicalMapper(result).map(result, MedicalWebModel.class));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    @PostMapping
    public BaseResponse create(@RequestBody MedicalWebModel report) {
        BaseResponse br = new BaseResponse();
        try {
            Medical result = medicalService.create(
                    getMedicalMapper(report).map(report, Medical.class),
                    report.getAttachments()
            );

            br.setData(getMedicalMapper(result).map(result, MedicalWebModel.class));

        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

    @PutMapping(UrlConstants.ID_PARAM)
    public BaseResponse update(
            @PathVariable long id,
            @RequestBody MedicalWebModel report
    ) {
        BaseResponse br = new BaseResponse();
        try {
            Medical result = medicalService.update(
                    id,
                    getMedicalMapper(report).map(report, Medical.class),
                    report.getAttachments()
            );

            br.setData(getMedicalMapper(result).map(result, MedicalWebModel.class));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

    @DeleteMapping(UrlConstants.ID_PARAM)
    public BaseResponse delete(@PathVariable long id) {
        BaseResponse br = new BaseResponse();
        try {
            medicalService.delete(id);
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

}
