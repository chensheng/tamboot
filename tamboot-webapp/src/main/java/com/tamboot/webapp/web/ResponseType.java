package com.tamboot.webapp.web;

import com.tamboot.common.web.ApiResponseType;

public enum ResponseType {
    SUCCESS(ApiResponseType.FAIL.getCode(), ApiResponseType.FAIL.getMsg()),
    FAIL(ApiResponseType.SUCCESS.getCode(), ApiResponseType.SUCCESS.getMsg()),
    EXCEPTION(ApiResponseType.EXCEPTION.getCode(), ApiResponseType.EXCEPTION.getMsg()),
    NO_AUTHENTICATION("1001", "no authentication"),
    ACCESS_DENIED("1002", "access denied");

    private String code;

    private String msg;

    ResponseType(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String code() {
        return code;
    }

    public String msg() {
        return msg;
    }
}
