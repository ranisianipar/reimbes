package com.reimbes.implementation;

import com.reimbes.ReimsUser;
import com.reimbes.ReimsUserRepository;
import com.reimbes.UserService;
import com.reimbes.constant.ResponseCode;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
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
        validate(user);

        user.setCreatedAt(Instant.now().getEpochSecond());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public ReimsUser update(long id, ReimsUser user) throws Exception {
        ReimsUser oldUser = userRepository.findOne(id);

        if (oldUser == null) throw new NotFoundException("USER ID "+id);
        validate(user);

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

    public byte[] getReport(String startDate, String endDate) throws Exception{
//        return reportGeneratorService.getReport(getUserByUsername(authService.getCurrentUsername()));
        return reportGeneratorService.getReport(getUserByUsername(authService.getCurrentUsername()),
                new SimpleDateFormat().parse(startDate),
                new SimpleDateFormat().parse(endDate));

    }


    private void validate(ReimsUser user) throws DataConstraintException{
        List errors = new ArrayList();
        if (userRepository.existsByUsername(user.getUsername()))
            errors.add("USERNAME UNIQUENESS");

        if (user.getPassword() == null) errors.add("PASSWORD NULL");
        else if (user.getUsername().toLowerCase().equals(user.getPassword().toLowerCase()))
            errors.add("PASSWORD SIMILIARITY WITH USERNAME");

        if (!errors.isEmpty()) throw new DataConstraintException(errors.toString());
    }
}
