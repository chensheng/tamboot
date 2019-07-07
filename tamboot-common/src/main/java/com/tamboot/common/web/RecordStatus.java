package com.tamboot.common.web;

public enum RecordStatus {
    DISABLED("0", "Disabled"),
    ENABLED("1", "Enabled");

    private String code;

    private String msg;

    RecordStatus(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
