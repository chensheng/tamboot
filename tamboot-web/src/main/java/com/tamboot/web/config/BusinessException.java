package com.tamboot.web.config;

public class BusinessException extends RuntimeException{
    private static final long serialVersionUID = -7710864994078530060L;

    private String code;

    public BusinessException() {
        this(TambootResponse.MSG_FAIL);
    }

    public BusinessException(String msg) {
        this(TambootResponse.CODE_FAIL, msg);
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
