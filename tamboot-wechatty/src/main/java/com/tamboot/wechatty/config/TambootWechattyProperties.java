package com.tamboot.wechatty.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tamboot.wechatty")
public class TambootWechattyProperties {
    private String token;

    private String aesKey;

    private String appId;

    private String appSecret;

    private boolean enableCryptedMode = true;

    private boolean autoUpdateAccessToken = false;

    private String accessTokenStrategyClass = "space.chensheng.wechatty.common.http.MemoryAccessTokenStrategy";

    private String payKey;

    private String payCertFile;

    private String payCertPassword;

    private String payMchId;

    private String payClientIp;

    private String payNotifyUrl;

    private String refundNotifyUrl;

    private int poolingHttpMaxPerRoute = 50;

    private int poolingHttpMaxTotal = 200;

    private int poolingHttpSocketTimeoutMillis = 10000;

    private int poolingHttpConnectTimeoutMillis = 1000;

    private int poolingHttpConnectionRequestTimeoutMillis = 1000;

    private boolean poolingHttpTcpNoDelay = true;

    private boolean poolingHttpProxyEnable = false;

    private String poolingHttpProxyHostname;

    private Integer poolingHttpProxyPort;

    private String poolingHttpProxyUsername;

    private String poolingHttpProxyPassword;

    private boolean enablePayCert = false;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public boolean isEnableCryptedMode() {
        return enableCryptedMode;
    }

    public void setEnableCryptedMode(boolean enableCryptedMode) {
        this.enableCryptedMode = enableCryptedMode;
    }

    public boolean isAutoUpdateAccessToken() {
        return autoUpdateAccessToken;
    }

    public void setAutoUpdateAccessToken(boolean autoUpdateAccessToken) {
        this.autoUpdateAccessToken = autoUpdateAccessToken;
    }

    public String getAccessTokenStrategyClass() {
        return accessTokenStrategyClass;
    }

    public void setAccessTokenStrategyClass(String accessTokenStrategyClass) {
        this.accessTokenStrategyClass = accessTokenStrategyClass;
    }

    public String getPayKey() {
        return payKey;
    }

    public void setPayKey(String payKey) {
        this.payKey = payKey;
    }

    public String getPayCertFile() {
        return payCertFile;
    }

    public void setPayCertFile(String payCertFile) {
        this.payCertFile = payCertFile;
    }

    public String getPayCertPassword() {
        return payCertPassword;
    }

    public void setPayCertPassword(String payCertPassword) {
        this.payCertPassword = payCertPassword;
    }

    public String getPayMchId() {
        return payMchId;
    }

    public void setPayMchId(String payMchId) {
        this.payMchId = payMchId;
    }

    public String getPayClientIp() {
        return payClientIp;
    }

    public void setPayClientIp(String payClientIp) {
        this.payClientIp = payClientIp;
    }

    public String getPayNotifyUrl() {
        return payNotifyUrl;
    }

    public void setPayNotifyUrl(String payNotifyUrl) {
        this.payNotifyUrl = payNotifyUrl;
    }

    public String getRefundNotifyUrl() {
        return refundNotifyUrl;
    }

    public void setRefundNotifyUrl(String refundNotifyUrl) {
        this.refundNotifyUrl = refundNotifyUrl;
    }

    public int getPoolingHttpMaxPerRoute() {
        return poolingHttpMaxPerRoute;
    }

    public void setPoolingHttpMaxPerRoute(int poolingHttpMaxPerRoute) {
        this.poolingHttpMaxPerRoute = poolingHttpMaxPerRoute;
    }

    public int getPoolingHttpMaxTotal() {
        return poolingHttpMaxTotal;
    }

    public void setPoolingHttpMaxTotal(int poolingHttpMaxTotal) {
        this.poolingHttpMaxTotal = poolingHttpMaxTotal;
    }

    public int getPoolingHttpSocketTimeoutMillis() {
        return poolingHttpSocketTimeoutMillis;
    }

    public void setPoolingHttpSocketTimeoutMillis(int poolingHttpSocketTimeoutMillis) {
        this.poolingHttpSocketTimeoutMillis = poolingHttpSocketTimeoutMillis;
    }

    public int getPoolingHttpConnectTimeoutMillis() {
        return poolingHttpConnectTimeoutMillis;
    }

    public void setPoolingHttpConnectTimeoutMillis(int poolingHttpConnectTimeoutMillis) {
        this.poolingHttpConnectTimeoutMillis = poolingHttpConnectTimeoutMillis;
    }

    public int getPoolingHttpConnectionRequestTimeoutMillis() {
        return poolingHttpConnectionRequestTimeoutMillis;
    }

    public void setPoolingHttpConnectionRequestTimeoutMillis(int poolingHttpConnectionRequestTimeoutMillis) {
        this.poolingHttpConnectionRequestTimeoutMillis = poolingHttpConnectionRequestTimeoutMillis;
    }

    public boolean isPoolingHttpTcpNoDelay() {
        return poolingHttpTcpNoDelay;
    }

    public void setPoolingHttpTcpNoDelay(boolean poolingHttpTcpNoDelay) {
        this.poolingHttpTcpNoDelay = poolingHttpTcpNoDelay;
    }

    public boolean isPoolingHttpProxyEnable() {
        return poolingHttpProxyEnable;
    }

    public void setPoolingHttpProxyEnable(boolean poolingHttpProxyEnable) {
        this.poolingHttpProxyEnable = poolingHttpProxyEnable;
    }

    public String getPoolingHttpProxyHostname() {
        return poolingHttpProxyHostname;
    }

    public void setPoolingHttpProxyHostname(String poolingHttpProxyHostname) {
        this.poolingHttpProxyHostname = poolingHttpProxyHostname;
    }

    public Integer getPoolingHttpProxyPort() {
        return poolingHttpProxyPort;
    }

    public void setPoolingHttpProxyPort(Integer poolingHttpProxyPort) {
        this.poolingHttpProxyPort = poolingHttpProxyPort;
    }

    public String getPoolingHttpProxyUsername() {
        return poolingHttpProxyUsername;
    }

    public void setPoolingHttpProxyUsername(String poolingHttpProxyUsername) {
        this.poolingHttpProxyUsername = poolingHttpProxyUsername;
    }

    public String getPoolingHttpProxyPassword() {
        return poolingHttpProxyPassword;
    }

    public void setPoolingHttpProxyPassword(String poolingHttpProxyPassword) {
        this.poolingHttpProxyPassword = poolingHttpProxyPassword;
    }

    public boolean isEnablePayCert() {
        return enablePayCert;
    }

    public void setEnablePayCert(boolean enablePayCert) {
        this.enablePayCert = enablePayCert;
    }
}
