package com.reimbes.implementation;

import com.reimbes.ReimsUser;
import com.reimbes.ReimsUserRepository;
import com.reimbes.UserService;
import com.reimbes.constant.ResponseCode;
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

import java.time.Instant;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private ReimsUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public ReimsUser create(ReimsUser user) throws ReimsException{
        // do validation
        if (userRepository.findByUsername(user.getUsername()) != null)
            throw new ReimsException("Username should be unique", HttpStatus.BAD_REQUEST, ResponseCode.BAD_REQUEST);

        if (user.getPassword() == null)
            throw new ReimsException("Password can't be null", HttpStatus.BAD_REQUEST, ResponseCode.BAD_REQUEST);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public ReimsUser update(long id, ReimsUser user) throws Exception {
        ReimsUser oldUser = userRepository.findOne(id);

        if (oldUser == null) throw new NotFoundException("USER with ID "+id);

        oldUser.setUsername(user.getUsername());
        oldUser.setPassword(user.getPassword());
        oldUser.setRole(user.getRole());
        oldUser.setUpdatedAt(Instant.now().getEpochSecond());
        return null;
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
}
