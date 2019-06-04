package com.tamboot.webapp.core;

import com.tamboot.web.config.TambootResponse;

public class ResponseEntity {
    public static final TambootResponse ACCESS_DENIED = new TambootResponse(ResponseType.ACCESS_DENIED.code(), ResponseType.ACCESS_DENIED.msg());

    public static final TambootResponse NO_AUTHENTICATION = new TambootResponse(ResponseType.NO_AUTHENTICATION.code(), ResponseType.NO_AUTHENTICATION.msg());

    public static final TambootResponse USERNAME_PASSWORD_ERROR = TambootResponse.fail("用户名或密码错误");

    public static final TambootResponse LOGIN_SUCCESS = TambootResponse.success("登录成功");
}
