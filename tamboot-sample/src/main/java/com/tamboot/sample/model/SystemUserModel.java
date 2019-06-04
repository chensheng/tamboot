package com.tamboot.sample.model;

import com.tamboot.webapp.core.BaseModel;

public class SystemUserModel extends BaseModel {
    private static final long serialVersionUID = -7367516553911828868L;

    private String username;

    private String password;

    private Integer status;

    private String roles;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
