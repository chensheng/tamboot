package com.tamboot.common.web;

import java.io.Serializable;

public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = -645219163683406043L;

    protected String code;

    protected String msg;

    protected T data;

    public ApiResponse() {
    }

    public ApiResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiResponse(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return ApiResponseType.SUCCESS.getCode().equals(code);
    }
}
