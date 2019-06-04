package com.tamboot.security.test.custom;

import com.tamboot.security.core.PasswordEncoderFactory;
import com.tamboot.security.core.TambootUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
    private PasswordEncoderFactory passwordEncoderFactory;

    public CustomUserDetailsService(PasswordEncoderFactory passwordEncoderFactory) {
        this.passwordEncoderFactory = passwordEncoderFactory;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("invalidUser".equals(username)) {
            throw new UsernameNotFoundException("");
        }

        return TambootUserDetails
                .init(1l, username, passwordEncoderFactory.get().encode("123456"))
                .roles("admin".equals(username) ? "ADMIN" : "USER")
                .build();
    }
}
