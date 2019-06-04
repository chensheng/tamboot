package com.tamboot.security.util;

import com.tamboot.security.core.TambootUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class SafeSecurityContextHolder {
    public static Long getUserId() {
        TambootUserDetails userDetails = getUserDetails();
        if (userDetails != null) {
            return userDetails.getUserId();
        }
        return null;
    }

    public static TambootUserDetails getUserDetails() {
        Authentication authentication = doGetAuthentication();
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal == null) {
            return null;
        }

        if (principal instanceof TambootUserDetails) {
            return (TambootUserDetails) principal;
        }

        if (UserDetails.class.isAssignableFrom(principal.getClass())) {
            UserDetails detail = (UserDetails) principal;
            return TambootUserDetails
                    .init(0l, detail.getUsername(), detail.getPassword())
                    .build();
        }

        return null;
    }

    public static Collection<? extends GrantedAuthority> getAuthorities() {
        Authentication authentication = doGetAuthentication();
        if (authentication == null) {
            return null;
        }

        return authentication.getAuthorities();
    }

    private static Authentication doGetAuthentication() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null) {
            return null;
        }

        return securityContext.getAuthentication();
    }
}
