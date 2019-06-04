package com.tamboot.web.test;

import java.io.Serializable;

public class TestDto implements Serializable {
    private static final long serialVersionUID = 6879156848051391911L;

    private String username;

    private int age;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
