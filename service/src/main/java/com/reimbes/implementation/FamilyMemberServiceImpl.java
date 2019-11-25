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
import org.springframework.data.domain.Page;
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

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AuthServiceImpl authService;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


    public FamilyMember create(Long userId, FamilyMember member) throws ReimsException {
        ReimsUser user = userService.get(userId);

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

    public FamilyMember getById(Long id) throws ReimsException {
        FamilyMember member = familyMemberRepository.findOne(id);
        if (member == null) throw new NotFoundException(member.getName());
        return member;
    }

    // ADMIN
    public FamilyMember getByUser(ReimsUser user, Long familyMemberId) throws ReimsException {
        FamilyMember familyMember = familyMemberRepository.findOne(familyMemberId);
        if (familyMember != null )
//            && familyMember.getFamilyMemberOf().getId() != user.getId()
            throw new NotFoundException("FAMILY_MEMBER");

        return familyMember;
    }

    public Page getAll(Pageable pageable) {
        ReimsUser user = authService.getCurrentUser();
        // ADMIN
        if (user.getRole() == ReimsUser.Role.ADMIN)
            return familyMemberRepository.findAll(pageable);

        // USER
        // return familyMemberRepository.findByFamilyMemberOf(user, pageable);
        return null;
    }

    // ADMIN
    public Page getAllByUser(ReimsUser user, Pageable pageable) {
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

    private void validate(FamilyMember data) throws DataConstraintException {
        // name?

        ArrayList errorMessages = new ArrayList();

        try {
            dateFormat.parse(data.getDateOfBirth()+"");
        }   catch (ParseException e) {
            errorMessages.add("INVALID_BIRTHDATE_FORMAT");
        }

        if (!errorMessages.isEmpty()) throw new DataConstraintException(errorMessages.toString());

    }
}
