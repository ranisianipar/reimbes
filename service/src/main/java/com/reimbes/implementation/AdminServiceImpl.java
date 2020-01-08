package com.reimbes.implementation;

import com.reimbes.AdminService;
import com.reimbes.FamilyMember;
import com.reimbes.Medical;
import com.reimbes.ReimsUser;
import com.reimbes.constant.ResponseCode;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    private static Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Utils utils;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private FamilyMemberServiceImpl familyMemberService;

    @Autowired
    private MedicalServiceImpl medicalService;

    @Override
    public Page getAllUser(String search, Pageable pageRequest) throws ReimsException {
        log.info(String.format("Get all users by criteria, search: %s", search));

        // tha page number default is 1, but querying things start from 0.
        int index = pageRequest.getPageNumber() - 1;

        if (index < 0) throw new NotFoundException("Page with negative index");
        Pageable pageable = new PageRequest(index, pageRequest.getPageSize(), pageRequest.getSort());

        return userService.getAllUsers(search, pageable);
    }

    @Override
    public ReimsUser getUser(long id) throws ReimsException {
        log.info(String.format("Get user with ID: %d", id));
        return userService.get(id);
    }

    @Override
    public ReimsUser createUser(ReimsUser user) throws ReimsException {
        validate(user);

        log.info(String.format("Create user."));
        return userService.create(user);
    }

    public Page<FamilyMember> getAllFamilyMember(Long userId, String name, Pageable page) throws ReimsException {
        log.info(String.format("GET all family member with criteria User Id: %d and Name: %s", userId, name));
        return familyMemberService.getAll(userId, name, page);
    }

    public FamilyMember getMember(long memberId) throws ReimsException {
        log.info(String.format("GET family member with ID: %d ", memberId));
        return familyMemberService.getById(memberId);
    }

    public FamilyMember createMember(long userId, FamilyMember member) throws ReimsException {
        log.info(String.format("Create family member for User with ID: %d", userId));
        return familyMemberService.create(userId, member);
    }

    public void deleteFamilyMember(long familyMemberId) throws ReimsException{
        log.info(String.format("Delete family member with ID: %d", familyMemberId));
        familyMemberService.delete(familyMemberId);
    }

    public FamilyMember updateMember(long familyMemberId, FamilyMember latestData, long userId) throws ReimsException {
        return familyMemberService.update(familyMemberId, latestData, userId);
    }

    @Override
    public ReimsUser updateUser(long id, ReimsUser user, HttpServletResponse response) throws ReimsException {
        ReimsUser currentUser = authService.getCurrentUser();

        validate(user);

        log.info("Check User update type, either his own data or other user");
        // if admin try to update his data
        if (currentUser.getId() == id) return userService.updateMyData(user, response);

        log.info(String.format("Update User data with ID: %d", id));
        return userService.update(id, user);
    }

    @Override
    public void deleteUser(long id) throws ReimsException{
        ReimsUser currentUser = authService.getCurrentUser();
        if (currentUser.getId() == id) throw new ReimsException("SELF_DELETION", HttpStatus.METHOD_NOT_ALLOWED, 405);

        log.info(String.format("Delete user with ID: %d", id));
        userService.delete(id);
    }

    public Page<Medical> getAllMedical(Pageable page, String title, Long start, Long end, Long userId)
            throws ReimsException {
        log.info(String.format("GET all medicals with criteria title: %s, time range: %d-%d, User Id: %d",
                title, start, end, userId));
        return medicalService.getAll(page, title, start, end, userId);
    }

    public Medical getMedical(long id) throws ReimsException {
        log.info(String.format("Get medical with ID:%d", id));
        return medicalService.get(id);
    }

    private void validate(ReimsUser newUser) throws ReimsException {
        log.info("Validate user data.");

        List<String> errors = new ArrayList<>();

        if (newUser.getRole() == null)
            errors.add("NULL_ATTRIBUTE_ROLE");
        else if (newUser.getRole() == ReimsUser.Role.USER && newUser.getGender() == null)
            errors.add("NULL_ATTRIBUTE_GENDER");

        if (newUser.getDateOfBirth() == null)
            errors.add("NULL_ATTRIBUTE_DATE_OF_BIRTH");

        if (!errors.isEmpty())
            throw new ReimsException(errors.toString(),HttpStatus.BAD_REQUEST, ResponseCode.BAD_REQUEST);
    }
}
