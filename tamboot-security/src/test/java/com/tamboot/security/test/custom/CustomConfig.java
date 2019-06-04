package com.tamboot.security.test.custom;

import com.tamboot.security.core.PasswordEncoderFactory;
import com.tamboot.security.permission.RoleBasedPermissionRepository;
import com.tamboot.security.token.TokenRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@TestConfiguration
public class CustomConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new CustomPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoderFactory passwordEncoderFactory) {
        return new CustomUserDetailsService(passwordEncoderFactory);
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public RoleBasedPermissionRepository roleBasedPermissionRepository() {
        return new CustomRoleBasedPermissionRepository();
    }

    @Bean
    public TokenRepository tokenRepository() {
        return new CustomTokenRepository();
    }
}
