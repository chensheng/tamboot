package com.tamboot.security.permission;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoleBasedPermission implements Serializable {
    private static final long serialVersionUID = 5709672139218507248L;

    private String urlAntPattern;

    private List<String> roles;

    public RoleBasedPermission(String urlAntPattern) {
        Assert.hasText(urlAntPattern, "urlAntPattern must not be empty");
        this.urlAntPattern = urlAntPattern;
        roles = new ArrayList<>();
    }

    public RoleBasedPermission addRole(String role) {
        Assert.hasText(role, "role must not be empty");
        roles.add("ROLE_" + role);
        return this;
    }

    public RoleBasedPermission addRoles(String... roles) {
        if (roles == null || roles.length == 0) {
            return this;
        }
        for (String role : roles) {
            addRole(role);
        }
        return this;
    }

    public String getUrlAntPattern() {
        return urlAntPattern;
    }

    public List<String> getRoles() {
        return roles;
    }

}
