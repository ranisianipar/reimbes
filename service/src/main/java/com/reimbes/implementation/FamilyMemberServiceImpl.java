package com.reimbes.implementation;

import com.reimbes.FamilyMember;
import com.reimbes.FamilyMemberRepository;
import com.reimbes.PatientRepository;
import com.reimbes.ReimsUser;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.MethodNotAllowedException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import com.reimbes.interfaces.AuthService;
import com.reimbes.interfaces.FamilyMemberService;
import com.reimbes.interfaces.UserService;
import com.reimbes.interfaces.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.reimbes.ReimsUser.Role.ADMIN;
import static com.reimbes.constant.General.NULL_USER_ID_CODE;

@Service
public class FamilyMemberServiceImpl implements FamilyMemberService {

    private static Logger log = LoggerFactory.getLogger(FamilyMemberServiceImpl.class);

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UtilsService utilsService;


    public FamilyMember create(Long userId, FamilyMember member) throws ReimsException {
        ReimsUser user = userService.get(userId);

        if (user.getRole() == ADMIN) return null;

        FamilyMember familyMember = FamilyMember.FamilyMemberBuilder()
                .familyMemberOf(user)
                .relationship(member.getRelationship())
                .dateOfBirth(member.getDateOfBirth())
                .name(member.getName())
                .build();

        validate(null, familyMember);

        familyMember.setCreatedAt(utilsService.getCurrentTime());

        log.info("Done mapping! FAMILY_MEMBER: " + familyMember.toString());

        return familyMemberRepository.save(familyMember);
    }

    public FamilyMember getById(Long id) throws ReimsException {
        ReimsUser currentUser = authService.getCurrentUser();
        FamilyMember member = familyMemberRepository.findOne(id);

        // only for ADMIN and related MEMBER
        if (currentUser.getRole() == ADMIN || member.getFamilyMemberOf().getId() == currentUser.getId())
            return member;

        else throw new NotFoundException(member.getName());
    }

    public Page getAll(Long userId, String nameFilter, Pageable pageRequest) throws ReimsException{
        /****************************************HANDLING REQUEST PARAM************************************************/
        int index = pageRequest.getPageNumber() - 1;
        if (index < 0) index = 0;
        Pageable pageable = new PageRequest(index, pageRequest.getPageSize(), pageRequest.getSort());

        // logic

        ReimsUser currentUser = authService.getCurrentUser();
        // ADMIN
        if (currentUser.getRole() == ADMIN) {
            log.info("[ADMIN] Query Family Member ");
            if (userId == null) return getAllByUser(null, nameFilter, pageable);
            return getAllByUser(userService.get(userId), nameFilter, pageable);
        }

        // USER
        return getAllByUser(currentUser, nameFilter, pageable);
    }

    public Page getAllByUser(ReimsUser searchUser, String name, Pageable pageable) {
        log.info("GET ALL family member WITH CRITERIA all users");
        if (searchUser == null) {
            log.info("[Query Activity] Query Family Member with no User");
            return familyMemberRepository.findByNameContainingIgnoreCase(name, pageable);
        }

        log.info("[Query Activity] Query Family Member with User -> user ID: " + searchUser.getId());

        return familyMemberRepository.findByFamilyMemberOfAndNameContainingIgnoreCase(searchUser, name, pageable);

    }


    public void delete(long id) throws ReimsException {
        ReimsUser currentUser = authService.getCurrentUser();
        if (currentUser.getRole() != ADMIN)
            throw new MethodNotAllowedException("Delete family member with ID " + id);
        familyMemberRepository.delete(id);
    }

    public FamilyMember update(long id, FamilyMember latestData, long userId) throws ReimsException {
        FamilyMember member = familyMemberRepository.findOne(id);

        latestData.setFamilyMemberOf(member.getFamilyMemberOf());

        if (userId != NULL_USER_ID_CODE) {
            ReimsUser user = userService.get(userId);
            if (user.getRole() != ADMIN)
                latestData.setFamilyMemberOf(user);
            else throw new DataConstraintException("Family Member can't be assigned to user with role ADMIN");
        }

        // validate
        validate(member, latestData);

        member.setName(latestData.getName());
        member.setRelationship(latestData.getRelationship());
        member.setDateOfBirth(latestData.getDateOfBirth());

        if (latestData.getFamilyMemberOf() != null) {
            member.setFamilyMemberOf(latestData.getFamilyMemberOf());
        }

        return familyMemberRepository.save(member);
    }

    private void validate(FamilyMember oldData, FamilyMember newData) throws DataConstraintException {
        List errors = new ArrayList();

        // Validate the credential data
        if (newData.getName() == null || newData.getName().isEmpty())
            errors.add("NULL_NAME");
        if (newData.getDateOfBirth() == null)
            errors.add("NULL_DATE_OF_BIRTH");
        if (newData.getRelationship() == null)
            errors.add("NULL_RELATIONSHIP");

        // compare new medicalUser data with other medicalUser data
        if (errors.isEmpty()){
            FamilyMember familyMember = familyMemberRepository.findByName(newData.getName());
            // update
            if (oldData != null && familyMember != null &&
                    familyMember.getFamilyMemberOf().getId() == oldData.getFamilyMemberOf().getId() &&
                    familyMember.getId() != oldData.getId())
                errors.add("UNIQUENESS_NAME");

                // create
            else if (oldData == null && familyMember != null &&
                    familyMember.getFamilyMemberOf().getId() == newData.getFamilyMemberOf().getId())
                errors.add("UNIQUENESS_NAME");
        }


        if (!errors.isEmpty()) throw new DataConstraintException(errors.toString());

    }
}
