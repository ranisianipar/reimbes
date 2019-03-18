package com.reimbes.implementation;

import com.reimbes.ReimsUser;
import com.reimbes.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ReimsUser reimsUser = userRepository.findByUsername(username);

        if (reimsUser == null ) {
            throw new UsernameNotFoundException(username);
        }

        return new UserDetailsImpl(reimsUser.getUsername(), reimsUser.getPassword(), Collections.emptyList());

    }
}
