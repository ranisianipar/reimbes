package com.reimbes.implementation;

import com.reimbes.*;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.MethodNotAllowedException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import com.reimbes.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.reimbes.ReimsUser.Role.ADMIN;
import static com.reimbes.constant.General.IDENTITY_CODE;
import static com.reimbes.constant.SecurityConstants.HEADER_STRING;

@Service
public class UserServiceImpl implements UserService {

    private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private ReportGeneratorServiceImpl reportGeneratorService;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private UtilsServiceImpl utilsServiceImpl;

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

        user.setName(user.getUsername()); // default
        user.setCreatedAt(utilsServiceImpl.getCurrentTime());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public ReimsUser update(long id, ReimsUser newUser) throws ReimsException {
        ReimsUser oldUser;

        if (id == IDENTITY_CODE) oldUser = userRepository.findByUsername(utilsServiceImpl.getPrincipalUsername());
        else oldUser = userRepository.findOne(id);

        if (oldUser == null) throw new NotFoundException("USER ID "+id);

        validate(newUser, oldUser);

        if (id != IDENTITY_CODE) oldUser.setRole(newUser.getRole());
        
        oldUser.setUsername(newUser.getUsername());
        if (newUser.getPassword() != null)
            oldUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        oldUser.setUpdatedAt(utilsServiceImpl.getCurrentTime());

        oldUser.setDivision(newUser.getDivision());
        oldUser.setGender(newUser.getGender());
        oldUser.setLicense(newUser.getLicense());
        oldUser.setVehicle(newUser.getVehicle());
        oldUser.setDateOfBirth(newUser.getDateOfBirth());

        log.info("[UPDATE] NEW DATE: " + oldUser.getDateOfBirth());
        return userRepository.save(oldUser);
    }

    @Override
    public ReimsUser updateMyData(ReimsUser user, HttpServletResponse response) throws ReimsException {
        ReimsUser userWithNewData = update(IDENTITY_CODE, user);

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

        if (id == IDENTITY_CODE) return userRepository.findByUsername(utilsServiceImpl.getPrincipalUsername());
        else user = userRepository.findOne(id);

        log.info(String.format("Get user with ID %d. Found => %s", id, user));
        if (user == null)
            throw new NotFoundException("USER "+id);
        return user;
    }

    @Override
    public Page getAllUsers(String username, Pageable pageable) {
        return userRepository.findByIdGreaterThanAndUsernameContainingIgnoreCase(IDENTITY_CODE, username, pageable);
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
    public byte[] getReport(Long start, Long end, String reimbursementType) throws Exception {
        ReimsUser currentUser = authService.getCurrentUser();
        if (currentUser.getRole() == ADMIN) throw new MethodNotAllowedException("Get Report");

        return reportGeneratorService.getReport(currentUser,
                start, end, reimbursementType.toLowerCase());
    }

    @Override
    public byte[] getImage(String imagePath) throws ReimsException {
        return utilsServiceImpl.getImage(authService.getCurrentUser(), imagePath);
    }


    /* Old User Data NOT NULL indicate update medicalUser activity */
    private void validate(ReimsUser newUserData, ReimsUser oldUserData) throws DataConstraintException{
        List errors = new ArrayList();

        // Validate the credential data
        if (newUserData.getUsername() == null || newUserData.getUsername().isEmpty())
            errors.add("NULL_USERNAME");
        // when create user
        if (oldUserData == null && (newUserData.getPassword() == null || newUserData.getPassword().isEmpty()))
            errors.add("NULL_PASSWORD");

        // compare new medicalUser data with other medicalUser data
        if (errors.isEmpty()){
            ReimsUser user = userRepository.findByUsername(newUserData.getUsername());

            // update
            if (oldUserData != null && user != null && user.getId() != oldUserData.getId())
                errors.add("UNIQUENESS_USERNAME");

            // create
            else if (oldUserData == null && user != null)
                errors.add("UNIQUENESS_USERNAME");
        }

        if (!errors.isEmpty()) throw new DataConstraintException(errors.toString());

    }


}
