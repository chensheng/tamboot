package com.tamboot.sample.constants;

public enum UserStatus {
    DISABLED(0),
    ENABLED(1);

    private int value;

    UserStatus(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
