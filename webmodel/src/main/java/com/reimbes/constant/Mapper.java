package com.reimbes.constant;

import com.reimbes.FamilyMember;
import com.reimbes.Medical;
import com.reimbes.ReimsUser;
import com.reimbes.response.FamilyMemberResponse;
import com.reimbes.response.MedicalWebModel;
import com.reimbes.response.Paging;
import com.reimbes.response.UserResponse;
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

    public static FamilyMemberResponse getFamilyMemberResponse(FamilyMember familyMember) {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        mapperFactory.classMap(FamilyMember.class, FamilyMemberResponse.class)
                .field("familyMemberOf.id", "familyMemberOf")
                .byDefault().register();

        return mapperFactory.getMapperFacade()
                .map(familyMember, FamilyMemberResponse.class);

    }

    public static List<? extends FamilyMemberResponse> getAllFamilyMemberResponses(List<FamilyMember> familyMembers) {
        log.info("Mapping object to web response...");
        List<FamilyMemberResponse> familyMemberResponses = new ArrayList<>();
        familyMembers.forEach(familyMember -> familyMemberResponses.add(getFamilyMemberResponse(familyMember)));

        return familyMemberResponses;
    }

    public static MapperFacade getUserResponseMapper() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(ReimsUser.class, UserResponse.class)
                .byDefault().register();
        return mapperFactory.getMapperFacade();
    }

    public static List<UserResponse> getAllUserResponses(List<ReimsUser> users) {
        List<UserResponse> userResponses = new ArrayList<>();
        users.forEach(user -> userResponses.add(getUserResponseMapper().map(user, UserResponse.class)));
        return userResponses;
    }
}
