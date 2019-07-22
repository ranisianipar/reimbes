package com.reimbes.implementation;

import com.reimbes.ReimsUser;
import com.reimbes.ReimsUserRepository;
import com.reimbes.UserService;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private ReportGeneratorServiceImpl reportGeneratorService;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private ReimsUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public ReimsUser create(ReimsUser user) throws ReimsException{
        validate(user, null);

        user.setCreatedAt(Instant.now().getEpochSecond());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public ReimsUser update(long id, ReimsUser user) throws Exception {
        ReimsUser oldUser = userRepository.findOne(id);

        if (oldUser == null) throw new NotFoundException("USER ID "+id);
        validate(user, oldUser);

        oldUser.setUsername(user.getUsername());
        oldUser.setPassword(user.getPassword());
        oldUser.setRole(user.getRole());
        oldUser.setUpdatedAt(Instant.now().getEpochSecond());
        return userRepository.save(oldUser);
    }

    @Override
    public ReimsUser getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public ReimsUser get(long id) throws ReimsException {
        ReimsUser user = userRepository.getOne(id);
        if (user == null)
            throw new NotFoundException("User with ID "+id);
        return userRepository.getOne(id);
    }

    @Override
    public Page getAllUsers(String username, Pageable pageable) {
        return userRepository.findByUsernameContaining(username, pageable);
    }

    @Override
    public void deleteUser(long id) {
        ReimsUser user = userRepository.findOne(id);

        if (user == null) {
            log.info("User with ID: "+id+" not found.");
            return;
        }

        // manually delete the transaction
        transactionService.deleteByUser(user);

        userRepository.delete(user);
    }


    public boolean isExist(String username) {
        return userRepository.existsByUsername(username);
    }

    public byte[] getReport(String startDate, String endDate) throws Exception {
        return reportGeneratorService.getReport(getUserByUsername(authService.getCurrentUsername()),
                DatatypeConverter.parseDateTime(startDate).getTimeInMillis(),
                DatatypeConverter.parseDateTime(endDate).getTimeInMillis()
        );

    }

    /* Old User Data NOT NULL indicate update user activity */
    private void validate(ReimsUser newUserData, ReimsUser oldUserData) throws DataConstraintException{
        List errors = new ArrayList();

        if (oldUserData != null) {
            if (userRepository.findByUsername(newUserData.getUsername()).getId() != oldUserData.getId())
                errors.add("USERNAME UNIQUENESS");
        } else if (userRepository.existsByUsername(newUserData.getUsername()))
            errors.add("USERNAME UNIQUENESS");

        if (newUserData.getPassword() == null) errors.add("PASSWORD NULL");
        else if (newUserData.getUsername().toLowerCase().equals(newUserData.getPassword().toLowerCase()))
            errors.add("PASSWORD SIMILIARITY WITH USERNAME");

        if (!errors.isEmpty()) throw new DataConstraintException(errors.toString());

    }


}
