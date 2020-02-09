package com.reimbes.implementation;

import com.reimbes.ReimsUser;
import com.reimbes.ReimsUserRepository;
import com.reimbes.Session;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import com.reimbes.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.reimbes.constant.General.IDENTITY_CODE;

@Service
public class UserServiceImpl implements UserService {

    private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private ReportGeneratorService reportGeneratorService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UtilsService utilsService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ReimsUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FamilyMemberService familyMemberService;


    @Override
    public ReimsUser create(ReimsUser user) throws ReimsException {
        validate(user, null);

        long currentTime = utilsService.getCurrentTime();

        user.setName(user.getUsername()); // default
        user.setCreatedAt(currentTime);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public ReimsUser update(long id, ReimsUser newUser) throws ReimsException {
        ReimsUser oldUser;

        if (id == IDENTITY_CODE) {
            oldUser = userRepository.findByUsername(utilsService.getPrincipalUsername());
        } else {
            oldUser = userRepository.findOne(id);
        }

        if (oldUser == null) {
            throw new NotFoundException("USER ID " + id);
        }

        validate(newUser, oldUser);

        long currentTime = utilsService.getCurrentTime();

        if (id != IDENTITY_CODE) {
            oldUser.setRole(newUser.getRole());
        }
        oldUser.setName(newUser.getUsername());
        oldUser.setUsername(newUser.getUsername());
        oldUser.setUpdatedAt(currentTime);
        oldUser.setDivision(newUser.getDivision());
        oldUser.setGender(newUser.getGender());
        oldUser.setLicense(newUser.getLicense());
        oldUser.setVehicle(newUser.getVehicle());
        oldUser.setDateOfBirth(newUser.getDateOfBirth());

        log.info("[UPDATE] NEW DATE: " + oldUser.getDateOfBirth());
        return userRepository.save(oldUser);
    }

    @Override
    public ReimsUser updateMyData(ReimsUser user, String token) throws ReimsException {
        ReimsUser userWithNewData = update(IDENTITY_CODE, user);
        Session session = Session.builder()
                .token(token)
                .username(user.getUsername())
                .build();
        authService.registerOrUpdateSession(session);
        return userWithNewData;
    }

    @Override
    public ReimsUser getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public ReimsUser get(long id) throws ReimsException {
        ReimsUser user;

        if (id == IDENTITY_CODE) {
            return userRepository.findByUsername(utilsService.getPrincipalUsername());
        } else {
            user = userRepository.findOne(id);
        }

        if (user == null) {
            throw new NotFoundException("USER " + id);
        }

        log.info(String.format("Get user with ID %d.", id));
        return user;
    }

    @Override
    public Page getAllUsers(String username, Pageable pageable) {
        return userRepository.findByIdGreaterThanAndUsernameContainingIgnoreCase(IDENTITY_CODE, username, pageable);
    }

    @Override
    public boolean delete(long id) {
        ReimsUser user = userRepository.findOne(id);
        if (user == null) {
            log.info("User with ID: ", id, " not found.");
            return false;
        }
        transactionService.deleteTransactionImageByUser(user);
        userRepository.delete(user);
        return true;
    }

    @Override
    public boolean isExist(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public String getReport(Long start, Long end, String reimbursementType) throws Exception {
        return reportGeneratorService.getReport(authService.getCurrentUser(), start, end, reimbursementType.toLowerCase());
    }

    /*
       This method return image in String, Base64 format
    */
    @Override
    public String getImage(String imagePath) throws ReimsException {
        log.info("GET Image by path");
        ReimsUser user = authService.getCurrentUser();
        try {
            if (isUserFile(imagePath, user)) {
                byte[] file = utilsService.getFile(imagePath);
                return Base64.getEncoder().encodeToString(file);
            }
            throw new NotFoundException("Image: " + imagePath);
        } catch (IOException e) {
            throw new NotFoundException(e.getMessage());
        }

    }

    @Override
    public boolean changePassword(String password) throws NotFoundException {
        ReimsUser user = authService.getCurrentUser();
        if (!isValidPassword(password)) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }

    private boolean isUserFile(String imagePath, ReimsUser user) {
        return imagePath.contains(String.format("/%d/", user.getId()));
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }

    /* Old User Data NOT NULL indicate update medicalUser activity */
    private void validate(ReimsUser newUserData, ReimsUser oldUserData) throws DataConstraintException {
        List errors = new ArrayList();

        // Validate the credential data
        if (newUserData.getUsername() == null || newUserData.getUsername().isEmpty())
            errors.add("NULL_USERNAME");
        // when create user
        if (oldUserData == null && (newUserData.getPassword() == null || newUserData.getPassword().isEmpty()))
            errors.add("NULL_PASSWORD");

        // compare new medicalUser data with other medicalUser data
        if (errors.isEmpty()) {
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
