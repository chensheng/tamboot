package com.tamboot.sample.model;

import com.tamboot.webapp.core.BaseModel;

public class SystemPermissionModel extends BaseModel {
    private static final long serialVersionUID = -5033595149426995526L;

    private String urlAntPattern;

    private String roles;

    public String getUrlAntPattern() {
        return urlAntPattern;
    }

    public void setUrlAntPattern(String urlAntPattern) {
        this.urlAntPattern = urlAntPattern;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
