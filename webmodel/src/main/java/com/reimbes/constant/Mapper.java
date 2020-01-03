package com.reimbes.constant;

import com.reimbes.Medical;
import com.reimbes.response.MedicalWebModel;
import com.reimbes.response.Paging;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class Mapper {

    private static Logger log = LoggerFactory.getLogger(Mapper.class);

    // mapping medicalResponse to medical entity
    public static MapperFacade getMedicalMapper(MedicalWebModel response) {
        log.info("Translate MedicalWebModel -> Medical");
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(MedicalWebModel.class, Medical.class)
                .byDefault()
                .register();
        mapperFactory.getMapperFacade().map(response, Medical.class);

        return mapperFactory.getMapperFacade();
    }

    // mapping medical entity medicalResponse
    public static MapperFacade getMedicalMapper(Medical response) {
        log.info("Translate Medical -> MedicalWebModel");
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Medical.class, MedicalWebModel.class)
                .byDefault().register();
        mapperFactory.getMapperFacade().map(response, MedicalWebModel.class);

        return mapperFactory.getMapperFacade();
    }

    // mapping medical entity medicalResponse
    public static List<MedicalWebModel> getAllMedicalResponse(List<Medical> medicals) {
        List<MedicalWebModel> medicalResponses = new ArrayList<>();
        medicals.forEach(medical -> medicalResponses.add(getMedicalMapper(medical).map(medical,MedicalWebModel.class)));
        return medicalResponses;
    }

    public static MapperFacade getPagingMapper() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Pageable.class, Paging.class)
                .byDefault().register();
        return mapperFactory.getMapperFacade();
    }
}
