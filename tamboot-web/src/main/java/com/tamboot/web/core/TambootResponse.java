package com.tamboot.web.core;

import com.tamboot.common.web.ApiResponse;
import com.tamboot.common.web.ApiResponseType;

public class TambootResponse extends ApiResponse<Object> {
    private static final long serialVersionUID = -9157793755569918888L;

    public TambootResponse() {
    }

    public TambootResponse(String code, String msg) {
        super(code, msg);
    }

    public TambootResponse(String code, String msg, Object data) {
        super(code, msg, data);
    }

    public static TambootResponse success(Object data) {
        return new TambootResponse(ApiResponseType.SUCCESS.getCode(), ApiResponseType.SUCCESS.getMsg(), data);
    }

    public static TambootResponse fail(String msg) {
        return new TambootResponse(ApiResponseType.FAIL.getCode(), msg);
    }

    public static TambootResponse exception() {
        return new TambootResponse(ApiResponseType.EXCEPTION.getCode(), ApiResponseType.EXCEPTION.getMsg());
    }

}
