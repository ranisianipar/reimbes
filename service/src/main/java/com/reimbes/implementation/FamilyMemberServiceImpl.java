package com.reimbes.implementation;

import com.reimbes.FamilyMember;
import com.reimbes.FamilyMemberRepository;
import com.reimbes.ReimsUser;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class FamilyMemberServiceImpl {

    private static Logger log = LoggerFactory.getLogger(FamilyMemberServiceImpl.class);

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


    public FamilyMember create(ReimsUser user, FamilyMember member) throws ReimsException {
        if (user.getRole() == ReimsUser.Role.ADMIN) return null;
        if (user.getGender() != ReimsUser.Gender.MALE)
            throw new DataConstraintException("GENDER_CONSTRAINT");

        FamilyMember familyMember = FamilyMember.builder()
                // .familyMemberOf(user)
                .dateOfBirth(member.getDateOfBirth())
                .name(member.getName())
                .relationship(member.getRelationship())
                .build();
        log.info("FAMILY_MEMBER: "+familyMember.toString());

        return familyMemberRepository.save(familyMember);
    }

    public FamilyMember get(ReimsUser user, Long familyMemberId) throws ReimsException {
        FamilyMember familyMember = familyMemberRepository.findOne(familyMemberId);
        if (familyMember != null )
//            && familyMember.getFamilyMemberOf().getId() != user.getId()
            throw new NotFoundException("FAMILY_MEMBER");

        return familyMember;
    }

    public List<FamilyMember> getAll(ReimsUser user, Pageable pageable) {

        if (user.getGender() != ReimsUser.Gender.MALE)
            return new ArrayList<>();

        // return familyMemberRepository.findByFamilyMemberOf(user, pageable);
        return null;
    }

    // throw error?
    public void delete(long id) {
        familyMemberRepository.delete(id);
    }


    public FamilyMember update(long id, FamilyMember latestData) {
        FamilyMember member = familyMemberRepository.findOne(id);

        // validate

        member.setName(latestData.getName());
        member.setRelationship(latestData.getRelationship());
        member.setDateOfBirth(latestData.getDateOfBirth());

        return familyMemberRepository.save(member);
    }

    public void validate(FamilyMember data) throws DataConstraintException {
        // name?

        ArrayList errorMessages = new ArrayList();

        try {
            dateFormat.parse(data.getDateOfBirth()+"");
        }   catch (ParseException e) {
            errorMessages.add("INVALID_BIRTHDATE_FORMAT");
        }

        throw new DataConstraintException(errorMessages.toString());

    }
}
