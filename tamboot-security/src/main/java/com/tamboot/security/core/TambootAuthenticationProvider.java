package com.tamboot.security.core;

import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TambootAuthenticationProvider extends DaoAuthenticationProvider {
    public TambootAuthenticationProvider(ApplicationContext applicationContext, PasswordEncoderFactory passwordEncoderFactory) {
        this.setUserDetailsService(new LazyUserDetailsService(applicationContext, passwordEncoderFactory));
        this.setPasswordEncoder(new LazyPasswordEncoder(passwordEncoderFactory));
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }

        String presentedPassword = authentication.getCredentials().toString();

        if (!getPasswordEncoder().matches(presentedPassword, userDetails.getPassword())) {
            logger.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        Authentication successAuthentication = super.createSuccessAuthentication(principal, authentication, user);
        if (successAuthentication == null) {
            return null;
        }

        if (!UsernamePasswordAuthenticationToken.class.isAssignableFrom(successAuthentication.getClass())) {
            return successAuthentication;
        }

        if (successAuthentication instanceof TambootUsernamePasswordAuthenticationToken) {
            return successAuthentication;
        }

        UsernamePasswordAuthenticationToken usernamePasswordToken = (UsernamePasswordAuthenticationToken) successAuthentication;
        return new TambootUsernamePasswordAuthenticationToken(usernamePasswordToken);
    }

    private static class LazyPasswordEncoder implements PasswordEncoder {
        private PasswordEncoderFactory factory;

        public LazyPasswordEncoder(PasswordEncoderFactory factory) {
            this.factory = factory;
        }

        @Override
        public String encode(CharSequence rawPassword) {
            return factory.get().encode(rawPassword);
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return factory.get().matches(rawPassword, encodedPassword);
        }

        @Override
        public boolean upgradeEncoding(String encodedPassword) {
            return factory.get().upgradeEncoding(encodedPassword);
        }
    }
}
