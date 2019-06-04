package com.tamboot.security.core;

import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class LazyAuthenticationManager implements AuthenticationManager {

    private ApplicationContext applicationContext;

    public LazyAuthenticationManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return applicationContext.getBean(AuthenticationManager.class).authenticate(authentication);
    }
}
