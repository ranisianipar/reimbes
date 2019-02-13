package com.reimbes;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
public class MyUserPrincipal implements UserDetails {

    final boolean enabled = true;
    final boolean accountNonExpired = true;
    final boolean credentialsNonExpired = true;
    final boolean accountNonLocked = true;


    private String username;
    private String password;
    private User.Role role;


    public MyUserPrincipal(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();
        setAuths.add(new SimpleGrantedAuthority(this.getRole().toString()));
        List<GrantedAuthority> Result = new ArrayList<GrantedAuthority>(setAuths);
        return Result;
    }
}
