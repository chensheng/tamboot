package com.tamboot.security.token;

import com.tamboot.security.config.TambootSecurityProperties;
import com.tamboot.security.util.CookieUtils;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieTokenPresenter extends TokenPresenter {
    public static final String COOKIE_NAME_TOKEN = "token";

    private TambootSecurityProperties properties;

    public CookieTokenPresenter(TambootSecurityProperties properties) {
        Assert.notNull(properties, "properties must not be null");
        this.properties = properties;
    }

    @Override
    public String getName() {
        return "COOKIE";
    }

    @Override
    public String readFromRequest(HttpServletRequest request) {
        return CookieUtils.getCookieValue(request, COOKIE_NAME_TOKEN);
    }

    @Override
    public String readFromResponse(HttpServletRequest request, HttpServletResponse response) {
        return CookieUtils.getSetCookieValue(response, COOKIE_NAME_TOKEN);
    }

    @Override
    public void write(HttpServletRequest request, HttpServletResponse response, String token) {
        CookieUtils.setCookie(request, response, COOKIE_NAME_TOKEN, token, properties.getTokenExpirySeconds());
    }

    @Override
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, COOKIE_NAME_TOKEN);
    }
}
