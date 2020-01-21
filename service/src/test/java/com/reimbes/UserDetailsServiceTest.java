package com.reimbes;

import com.reimbes.implementation.UserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.gen5.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = UserDetailsServiceImpl.class)
public class UserDetailsServiceTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private ReimsUserRepository userRepository;

    private ReimsUser user;

    @Before
    public void setup() {
        user = ReimsUser.ReimsUserBuilder()
                .username("TEST")
                .password("TEST")
                .role(ReimsUser.Role.ADMIN)
                .build();
    }

    @Test
    public void returnUserByUsername() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        // will be useful, if users have multi authorities
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
        UserDetailsImpl expectedResult = new UserDetailsImpl(user, authorities);

        assertEquals(expectedResult, userDetailsService.loadUserByUsername(user.getUsername()));
    }

    @Test
    public void errorThrows_whenUnexistUsernameLoaded() {
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(user.getUsername()));
    }


}
