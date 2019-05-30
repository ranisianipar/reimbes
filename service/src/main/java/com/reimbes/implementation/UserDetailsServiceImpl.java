package com.reimbes.implementation;

import com.reimbes.ReimsUser;
import com.reimbes.ReimsUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ReimsUserRepository reimsUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ReimsUser reimsUser = reimsUserRepository.findByUsername(username);

        if (reimsUser == null ) {
            throw new UsernameNotFoundException(username);
        }
        // will be useful, if users have multi authorities
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(reimsUser.getRole().toString()));

        return new UserDetailsImpl(reimsUser, authorities);

    }
}
