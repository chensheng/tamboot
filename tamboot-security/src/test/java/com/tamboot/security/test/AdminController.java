package com.tamboot.security.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/name")
    public String name() {
        return "admin";
    }
}
