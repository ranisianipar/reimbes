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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.reimbes.ReimsUser.Role.ADMIN;
import static com.reimbes.constant.General.*;

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
        if (!isAllowedToAccess(user)) {
            throw new DataConstraintException("Family Member can't be assigned ADMIN or may be user has reach family member limit");
        }
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
        if (currentUser.getRole() == ADMIN || member.getFamilyMemberOf().getId() == currentUser.getId()) {
            return member;
        } else {
            throw new NotFoundException(member.getName());
        }
    }

    public Page getAll(Long userId, String nameFilter, Pageable pageRequest) throws ReimsException {
        Pageable pageable = utilsService.getPageRequest(pageRequest);
        ReimsUser currentUser = authService.getCurrentUser();
        if (isAdmin(currentUser)) {
            log.info("[ADMIN] Query Family Member ");
            if (userId == null) {
                return getAllByUser(null, nameFilter, pageable);
            }
            return getAllByUser(userService.get(userId), nameFilter, pageable);
        }
        return getAllByUser(currentUser, nameFilter, pageable);
    }

    public Page getAllByUser(ReimsUser searchUser, String name, Pageable pageable) {
        log.info("GET ALL family member WITH CRITERIA all users");
        if (searchUser == null) {
            return familyMemberRepository.findByNameContainingIgnoreCase(name, pageable);
        }
        return familyMemberRepository.findByFamilyMemberOfAndNameContainingIgnoreCase(searchUser, name, pageable);

    }


    public void delete(long id) throws ReimsException {
        ReimsUser currentUser = authService.getCurrentUser();
        FamilyMember member = familyMemberRepository.findOne(id);
        if (member.getFamilyMemberOf() != currentUser) {
            throw new MethodNotAllowedException("Delete family member with ID " + id);
        }
        familyMemberRepository.delete(id);
    }

    public FamilyMember update(long id, FamilyMember latestData, long userId) throws ReimsException {
        FamilyMember member = familyMemberRepository.findOne(id);
        latestData.setFamilyMemberOf(member.getFamilyMemberOf());
        if (userId != NULL_USER_ID_CODE) {
            ReimsUser user = userService.get(userId);
            if (!isAdmin(user)) {
                latestData.setFamilyMemberOf(user);
            } else {
                throw new DataConstraintException("Family Member can't be assigned to user with role ADMIN");
            }
        }
        validate(member, latestData);
        member.setName(latestData.getName());
        member.setDateOfBirth(latestData.getDateOfBirth());
        if (latestData.getFamilyMemberOf() != null) {
            member.setFamilyMemberOf(latestData.getFamilyMemberOf());
        }
        return familyMemberRepository.save(member);
    }

    private boolean isSufficientFamilyMemberSpace(ReimsUser user) {
        int countFamilyMember = familyMemberRepository.countByFamilyMemberOf(user);
        return (countFamilyMember < FAMILY_MEMBER_LIMIT);
    }

    private boolean isAllowedToAccess(ReimsUser user) {
        return (!isAdmin(user) && isSufficientFamilyMemberSpace(user));
    }

    private boolean isAdmin(ReimsUser user) {
        return user.getRole() == ADMIN;
    }

    private void validate(FamilyMember oldData, FamilyMember newData) throws DataConstraintException {
        List<String> errors = new ArrayList();

        // Validate the credential data
        if (newData.getName() == null || newData.getName().isEmpty()) {
            errors.add(NULL_NAME);
        }
        if (newData.getDateOfBirth() == null) {
            errors.add(NULL_DATE_OF_BIRTH);
        }
        if (newData.getRelationship() == null) {
            errors.add(NULL_RELATIONSHIP);
        }

        // compare new medicalUser data with other medicalUser data
        if (errors.isEmpty()) {
            FamilyMember familyMember = familyMemberRepository.findByName(newData.getName());
            // update
            if ((oldData != null) && (familyMember != null) && (familyMember.getFamilyMemberOf().getId() == oldData.getFamilyMemberOf().getId()) && (familyMember.getId() != oldData.getId())) {
                errors.add(UNIQUENESS_NAME);
            } else if ((oldData == null) && (familyMember != null) && (familyMember.getFamilyMemberOf().getId() == newData.getFamilyMemberOf().getId())) {
                // create
                errors.add(UNIQUENESS_NAME);
            }

        }
        if (!errors.isEmpty()) {
            throw new DataConstraintException(errors.toString());
        }

    }
}
