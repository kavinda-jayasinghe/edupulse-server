package com.info_labs.edupulse.security;

import com.info_labs.edupulse.entity.User;
import com.info_labs.edupulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String mobile) throws UsernameNotFoundException {
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + mobile));

        String role = "ROLE_" + user.getProfileType().name(); // e.g. ROLE_ADMIN, ROLE_TEACHER, ROLE_STUDENT

        return new org.springframework.security.core.userdetails.User(
                user.getMobile(),
                user.getPassword(),
                user.isEnabled(),
                true, true, true,
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}
