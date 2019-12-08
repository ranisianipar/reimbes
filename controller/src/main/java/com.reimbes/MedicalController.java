package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.MedicalServiceImpl;
import com.reimbes.response.BaseResponse;
import com.reimbes.response.MedicalWebModel;
import com.reimbes.response.TransactionResponse;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@CrossOrigin(origins = UrlConstants.CROSS_ORIGIN_URL)
@RestController
@RequestMapping(UrlConstants.API_PREFIX + UrlConstants.MEDICAL_PREFIX)
public class MedicalController {
    private static Logger log = LoggerFactory.getLogger(MedicalController.class);

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

        br.setData(getAllMedicalResponse(
                medicalService.getAll(pageRequest, search, start, end).getContent()
        ));
        return br;
    }

    @GetMapping(UrlConstants.ID_PARAM)
    public BaseResponse get(@PathVariable long id) {
        BaseResponse br = new BaseResponse();
        try {
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
            log.info("attachment: "+ report.getAttachments());

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



    // mapping medicalResponse to medical entity
    private MapperFacade getMedicalMapper(MedicalWebModel response) {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(MedicalWebModel.class, Medical.class)
                .byDefault()
                .register();
        mapperFactory.getMapperFacade().map(response, MedicalWebModel.class);

        return mapperFactory.getMapperFacade();
    }

    // mapping medical entity medicalResponse
    private MapperFacade getMedicalMapper(Medical response) {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Medical.class, MedicalWebModel.class)
                .byDefault().register();
        mapperFactory.getMapperFacade().map(response, MedicalWebModel.class);

        return mapperFactory.getMapperFacade();
    }

    // mapping medical entity medicalResponse
    private List<MedicalWebModel> getAllMedicalResponse(List<Medical> medicals) {
        List<MedicalWebModel> medicalResponses = new ArrayList<>();
        Iterator<Medical> iterator = medicals.iterator();
        Medical medical;
        while (iterator.hasNext()) {
            medical = iterator.next();
            medicalResponses.add(getMedicalMapper(medical).map(medical,MedicalWebModel.class));
        }

        return medicalResponses;
    }
}
