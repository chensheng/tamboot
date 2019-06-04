package com.tamboot.security.test.custom;

import com.tamboot.security.core.TambootUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        TambootUserDetails userDetails = (TambootUserDetails) authentication.getPrincipal();
        response.getWriter().write("login success " + userDetails.getUsername());
    }
}
