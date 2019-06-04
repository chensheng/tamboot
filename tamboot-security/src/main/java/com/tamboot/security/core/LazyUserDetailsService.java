package com.tamboot.security.core;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.util.StringUtils;

import java.util.List;

public class LazyUserDetailsService implements UserDetailsService {
    private ApplicationContext applicationContext;

    private PasswordEncoderFactory passwordEncoderFactory;

    private volatile UserDetailsService defaultUserDetailsService;

    public LazyUserDetailsService(ApplicationContext applicationContext, PasswordEncoderFactory passwordEncoderFactory) {
        this.applicationContext = applicationContext;
        this.passwordEncoderFactory = passwordEncoderFactory;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return applicationContext.getBean(UserDetailsService.class).loadUserByUsername(username);
        } catch (BeansException e) {
            return getDefault().loadUserByUsername(username);
        }

    }

    private UserDetailsService getDefault() {
        if (defaultUserDetailsService == null) {
            synchronized (this) {
                if (defaultUserDetailsService == null) {
                    defaultUserDetailsService = inMemoryUserDetailsManager();
                }
            }
        }
        return defaultUserDetailsService;
    }

    private InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        try {
            SecurityProperties properties = applicationContext.getBean(SecurityProperties.class);
            SecurityProperties.User user = properties.getUser();
            List<String> roles = user.getRoles();
            return new InMemoryUserDetailsManager(User
                    .withUsername(user.getName())
                    .password(passwordEncoderFactory.get().encode(user.getPassword()))
                    .roles(StringUtils.toStringArray(roles)).build());
        } catch (BeansException e) {
            return new InMemoryUserDetailsManager();
        }
    }
}
