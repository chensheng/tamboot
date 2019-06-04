package com.tamboot.web.config;

import java.io.Serializable;

public class TambootResponse implements Serializable {
    private static final long serialVersionUID = -3397113136576776613L;

    public static final String CODE_FAIL = "0";

    public static final String CODE_SUCCESS = "1";

    public static final String CODE_EXCEPTION = "9999";

    public static final String MSG_SUCCESS = "success";

    public static final String MSG_FAIL = "fail";

    public static final String MSG_EXCEPTION = "exception";

    private String code;

    private String msg;

    private Object data;

    public TambootResponse() {
    }

    public TambootResponse(String code, String msg) {
        this(code, msg, null);
    }

    public TambootResponse(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static TambootResponse success(Object data) {
        return new TambootResponse(CODE_SUCCESS, MSG_SUCCESS, data);
    }

    public static TambootResponse fail(String msg) {
        return new TambootResponse(CODE_FAIL, msg, null);
    }

    public static TambootResponse exception() {
        return new TambootResponse(CODE_EXCEPTION, MSG_EXCEPTION, null);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
