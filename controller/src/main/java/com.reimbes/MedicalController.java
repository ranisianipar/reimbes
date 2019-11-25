package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.MedicalServiceImpl;
import com.reimbes.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = UrlConstants.CROSS_ORIGIN_URL)
@RestController
@RequestMapping(UrlConstants.API_PREFIX + UrlConstants.MEDICAL_PREFIX)
public class MedicalController {

    @Autowired
    private MedicalServiceImpl medicalService;

    @GetMapping
    public BaseResponse getAll(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "date") String sortBy,
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end,
            @RequestParam (value = "search", required = false) String search
    ) {
        BaseResponse br = new BaseResponse();
        Pageable pageRequest = new PageRequest(page, size, new Sort(Sort.Direction.ASC, sortBy));

        br.setData(medicalService.getAll(pageRequest, search, start, end).getContent());
        return br;
    }

    @GetMapping(UrlConstants.ID_PARAM)
    public BaseResponse get(@PathVariable long id) {
        BaseResponse br = new BaseResponse();
        try {
            br.setData(medicalService.get(id));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    @PostMapping
    public BaseResponse create(
            @RequestBody Medical report,
            @RequestParam(value = "attachement", required = false) MultipartFile attachement
    ) {
        BaseResponse br = new BaseResponse();
        try {
            br.setData(medicalService.create(report, attachement));
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

    @PutMapping(UrlConstants.ID_PARAM)
    public BaseResponse update(
            @PathVariable long id,
            @RequestBody Medical report,
            @RequestParam(value = "attachement", required = false) MultipartFile attachement) {
        BaseResponse br = new BaseResponse();
        try {
            br.setData(medicalService.update(id, report, attachement));
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
