package com.reimbes.implementation;

import com.reimbes.FamilyMember;
import com.reimbes.FamilyMemberRepository;
import com.reimbes.ReimsUser;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FamilyMemberServiceImpl {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;


    public FamilyMember create(ReimsUser user, FamilyMember member) throws ReimsException {
        if (user.getGender() != ReimsUser.Gender.MALE)
            throw new DataConstraintException("GENDER_CONSTRAINT");

        FamilyMember familyMember = new FamilyMember();
//        familyMember.setFamilyMemberOf(user);
        familyMember.setDateOfBirth(member.getDateOfBirth());
        familyMember.setName(member.getName());
        familyMember.setRelationship(member.getRelationship());
        return familyMemberRepository.save(familyMember);
    }

    public FamilyMember get(ReimsUser user, Long familyMemberId) throws ReimsException {
        FamilyMember familyMember = familyMemberRepository.findOne(familyMemberId);
//        if (familyMember != null && familyMember.getFamilyMemberOf().getId() != user.getId())
//            throw new NotFoundException("FAMILY_MEMBER");

        return familyMember;
    }

    public List<FamilyMember> getAll(ReimsUser user, Pageable pageable) {

        if (user.getGender() != ReimsUser.Gender.MALE)
            return new ArrayList<>();

//        return familyMemberRepository.findByFamilyMemberOf(user, pageable);
        return new ArrayList<>();
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

    public void validate(FamilyMember data) {
        // name?
    }
}
