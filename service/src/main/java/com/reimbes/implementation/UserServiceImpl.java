package com.reimbes.implementation;

import com.reimbes.ReimsUser;
import com.reimbes.ReimsUserRepository;
import com.reimbes.UserDetailsImpl;
import com.reimbes.UserService;
import com.reimbes.constant.SecurityConstants;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
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
    public ReimsUser create(ReimsUser user) throws ReimsException {
        validate(user, null);

        user.setCreatedAt(Instant.now().getEpochSecond());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public ReimsUser update(long id, ReimsUser user, HttpServletResponse response) throws ReimsException {
        ReimsUser oldUser;

        if (id == 0) {
            oldUser = userRepository.findByUsername(authService.getCurrentUsername());
        } else {
            oldUser = userRepository.findOne(id);
            oldUser.setRole(user.getRole());
        }

        if (oldUser == null) throw new NotFoundException("USER ID "+id);

        validate(user, oldUser);

        oldUser.setUsername(user.getUsername());
        oldUser.setPassword(passwordEncoder.encode(user.getPassword()));
        oldUser.setUpdatedAt(Instant.now().getEpochSecond());

        // method for editing personal data
        if (response != null) {
            // update token with latest username

            Collection authorities =  new ArrayList();

            authorities.add(new SimpleGrantedAuthority(oldUser.getRole().toString()));

            // add token
            String token = authService.generateToken(new UserDetailsImpl(user, authorities), authorities);

            // register token
            authService.registerToken(token);

            // return response with NEW token
            response.setHeader(SecurityConstants.HEADER_STRING, token);
        }

        return userRepository.save(oldUser);
    }

    @Override
    public ReimsUser getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public ReimsUser get(long id) throws ReimsException {
        ReimsUser user;
        if (id == 0) return userRepository.findByUsername(authService.getCurrentUsername());
        else user = userRepository.findOne(id);
        if (user == null)
            throw new NotFoundException("USER "+id);
        return user;
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
        Long start;
        Long end;

        if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
            start = null;
            end = null;
        } else {
            start = Long.parseLong(startDate);
            end = Long.parseLong(endDate);
        }
        return reportGeneratorService.getReport(getUserByUsername(authService.getCurrentUsername()),
                start, end);
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
            if (oldUserData != null){
                ReimsUser user = userRepository.findByUsername(newUserData.getUsername());
                if (user != null && user.getId() != oldUserData.getId())
                    errors.add("UNIQUENESS_USERNAME");
            }

            if (newUserData.getUsername().toLowerCase().equals(newUserData.getPassword().toLowerCase()))
                errors.add("INSECURE_PASSWORD");
        }

        if (!errors.isEmpty()) throw new DataConstraintException(errors.toString());

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Just load the reimsUser by its username");
        ReimsUser reimsUser = userRepository.findByUsername(username);

        if (reimsUser == null ) {
            log.info("User not found, username: "+username);
            throw new UsernameNotFoundException(username);
        }

        log.info("USER: "+reimsUser.getUsername());

        // will be useful, if users have multi authorities
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(reimsUser.getRole().toString()));


        return new UserDetailsImpl(reimsUser, authorities);
    }

}
