package com.tamboot.security.test;

import com.tamboot.security.core.TambootAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/anonymous")
public class AnonymousController {
    @Autowired
    private TambootAuthenticationService authenticationService;

    @GetMapping("/data")
    public String data() {
        return "data";
    }

    @PostMapping("/manuallyLogin")
    public String manuallyLogin(String username, HttpServletRequest request, HttpServletResponse response) {
        return authenticationService.login(username, request, response);
    }

    @PostMapping("/manuallyLogout")
    public String manuallyLogout(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.logout(request, response);
        return "success";
    }
}
