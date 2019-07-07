package com.tamboot.web.core;

import com.tamboot.common.web.ApiResponseType;

public class BusinessException extends RuntimeException{
    private static final long serialVersionUID = -7710864994078530060L;

    private String code;

    public BusinessException() {
        this(ApiResponseType.FAIL.getMsg());
    }

    public BusinessException(String msg) {
        this(ApiResponseType.FAIL.getCode(), msg);
    }

    public BusinessException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
