package com.tamboot.security.core;

import java.io.Serializable;

public class TambootWebAuthenticationDetails implements Serializable {
    private static final long serialVersionUID = -4979917136871354725L;

    private String remoteAddress;

    private String sessionId;

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
