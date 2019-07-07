package com.tamboot.common.web;

public enum ApiResponseType {
    FAIL("0", "fail"),
    SUCCESS("1", "success"),
    EXCEPTION("9999", "exception");

    private String code;

    private String msg;

    ApiResponseType(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
