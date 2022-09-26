package org.javaproteam27.socialnetwork.security;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.security.jwt.JwtUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return JwtUser.builder().userName(username).build();
    }
}
