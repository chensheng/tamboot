package com.tamboot.security.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping("/name")
    public String name() {
        return "manager";
    }
}
