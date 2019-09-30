package com.reimbes.implementation;

import com.reimbes.*;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.reimbes.constant.General.SPECIAL_ID;
import static com.reimbes.constant.SecurityConstants.HEADER_STRING;

@Service
public class UserServiceImpl implements UserService {

    private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private ReportGeneratorServiceImpl reportGeneratorService;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private Utils utils;

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private ReimsUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FamilyMemberServiceImpl familyMemberService;


    @Override
    public ReimsUser create(ReimsUser user) throws ReimsException {
        validate(user, null);

        user.setCreatedAt(Instant.now().toEpochMilli());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public ReimsUser update(long id, ReimsUser user) throws ReimsException {
        ReimsUser oldUser;


        if (id == SPECIAL_ID) oldUser = userRepository.findByUsername(utils.getUsername());
        else oldUser = userRepository.findOne(id);

        if (oldUser == null) throw new NotFoundException("USER ID "+id);

        validate(user, oldUser);

        if (id != SPECIAL_ID) oldUser.setRole(user.getRole());
        
        oldUser.setUsername(user.getUsername());
        oldUser.setPassword(passwordEncoder.encode(user.getPassword()));
        oldUser.setUpdatedAt(Instant.now().toEpochMilli());

        return userRepository.save(oldUser);
    }

    @Override
    public ReimsUser updateMyData(ReimsUser user, HttpServletResponse response) throws ReimsException {
        ReimsUser userWithNewData = update(SPECIAL_ID, user);

        // update token with latest username
        Collection authorities =  new ArrayList();

        authorities.add(new SimpleGrantedAuthority(userWithNewData.getRole().toString()));

        // add token
        String token = authService.generateToken(new UserDetailsImpl(user, authorities), authorities);

        // register token
        authService.registerToken(token);

        response.setHeader(HEADER_STRING, token);

        // Modify Login Response

        return userWithNewData;

    }

    @Override
    public ReimsUser getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public ReimsUser get(long id) throws ReimsException {
        ReimsUser user;

        if (id == SPECIAL_ID) return userRepository.findByUsername(utils.getUsername());
        else user = userRepository.findOne(id);
        if (user == null)
            throw new NotFoundException("USER "+id);
        return user;
    }

    @Override
    public Page getAllUsers(String username, Pageable pageable) {
        return userRepository.findByIdGreaterThanAndUsernameContainingIgnoreCase(SPECIAL_ID, username, pageable);
    }

    @Override
    public void delete(long id) {
        ReimsUser user = userRepository.findOne(id);

        if (user == null) {
            log.info("User with ID: "+id+" not found.");
            return;
        }
        // manually delete the transaction
        transactionService.deleteByUser(user);

        userRepository.delete(user);
    }

    @Override
    public boolean isExist(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public byte[] getReport(String startDate, String endDate) throws Exception {
        Long start;
        Long end;



        if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
            start = null;
            end = null;
        } else {
            start = Long.parseLong(startDate);
            end = Long.parseLong(endDate);
        }
        return reportGeneratorService.getReport(getUserByUsername(utils.getUsername()),
                start, end);
    }

    // accessed by Admin
    public FamilyMember addFamilyMember(long id, FamilyMember member) throws ReimsException {
        return familyMemberService.create(userRepository.findOne(id), member);
    }

    public List<FamilyMember> getAllFamilyMember(Long id, Pageable pageable) {
        return familyMemberService.getAll(userRepository.findOne(id), pageable);
    }

    // accessed by Admin and User
    public FamilyMember getFamilyMember(Long userId, Long memberId) throws ReimsException {
        if (userId == null)
            return familyMemberService.get(userRepository.findByUsername(utils.getUsername()), memberId);

        return familyMemberService.get(userRepository.findOne(userId), memberId);
    }


    /* Old User Data NOT NULL indicate update user activity */
    private void validate(ReimsUser newUserData, ReimsUser oldUserData) throws DataConstraintException{
        List errors = new ArrayList();

        // Validate the credential data
        if (newUserData.getUsername() == null || newUserData.getUsername().isEmpty())
            errors.add("NULL_USERNAME");
        if (newUserData.getPassword() == null || newUserData.getPassword().isEmpty())
            errors.add("NULL_PASSWORD");

        // compare new user data with other user data
        if (errors.isEmpty()){
            ReimsUser user = userRepository.findByUsername(newUserData.getUsername());

            // update
            if (oldUserData != null && user != null && user.getId() != oldUserData.getId())
                errors.add("UNIQUENESS_USERNAME");

            // create
            else if (oldUserData == null && user != null)
                errors.add("UNIQUENESS_USERNAME");

            if (newUserData.getUsername().toLowerCase().equals(newUserData.getPassword().toLowerCase()))
                errors.add("INSECURE_PASSWORD");
        }

        if (!errors.isEmpty()) throw new DataConstraintException(errors.toString());

    }


}
