package com.tamboot.web.test;

import javax.validation.constraints.NotEmpty;

public class TestForm {
    @NotEmpty(message = "username must not be null")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
