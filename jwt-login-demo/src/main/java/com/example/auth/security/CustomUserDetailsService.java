package com.example.auth.security;

import com.example.auth.user.User;
import com.example.auth.user.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repository;
    public CustomUserDetailsService(UserRepository repository) { this.repository = repository; }
    @Override public UserDetails loadUserByUsername(String username) {
        User user = repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(user.getRole()))).build();
    }
}
