package com.tamboot.security.core;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public class RoleGrantedAuthority implements GrantedAuthority {
    private static final long serialVersionUID = -4985069197573862938L;

    private String role;

    public RoleGrantedAuthority() {
    }

    public RoleGrantedAuthority(String role) {
        Assert.hasText(role, "A granted authority textual representation is required");
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role;
    }

    public void setAuthority(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof RoleGrantedAuthority) {
            return role.equals(((RoleGrantedAuthority) obj).role);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.role.hashCode();
    }

    @Override
    public String toString() {
        return this.role;
    }
}
