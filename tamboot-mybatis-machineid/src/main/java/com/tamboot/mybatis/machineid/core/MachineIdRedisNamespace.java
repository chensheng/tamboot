package com.tamboot.mybatis.machineid.core;

public enum MachineIdRedisNamespace {
    DYNAMIC("dynamic", "dynamic machine id");

    private String code;

    private String msg;

    MachineIdRedisNamespace(String code, String msg) {
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
