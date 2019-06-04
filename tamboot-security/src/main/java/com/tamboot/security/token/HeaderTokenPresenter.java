package com.tamboot.security.token;

import com.tamboot.security.config.TambootSecurityProperties;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HeaderTokenPresenter extends TokenPresenter {
    public static final String HEADER_NAME_TOKEN = "Token";

    private TambootSecurityProperties properties;

    public HeaderTokenPresenter(TambootSecurityProperties properties) {
        Assert.notNull(properties, "properties must not be null");
        this.properties = properties;
    }

    @Override
    public String getName() {
        return "HEADER";
    }

    @Override
    public String readFromRequest(HttpServletRequest request) {
        return request.getHeader(HEADER_NAME_TOKEN);
    }

    @Override
    public String readFromResponse(HttpServletRequest request, HttpServletResponse response) {
        return response.getHeader(HEADER_NAME_TOKEN);
    }

    @Override
    public void write(HttpServletRequest request, HttpServletResponse response, String token) {
        response.setHeader(HEADER_NAME_TOKEN, token);
    }

    @Override
    public void delete(HttpServletRequest request, HttpServletResponse response) {
    }
}
