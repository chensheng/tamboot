package com.tamboot.sample.controller;

import com.tamboot.sample.service.SystemUserService;
import com.tamboot.security.core.TambootUserDetails;
import com.tamboot.sample.form.ModifyPasswordForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private SystemUserService systemUserService;

    @GetMapping("/profile")
    public TambootUserDetails profile() {
        return systemUserService.findProfile();
    }

    @PostMapping("/modifyPassword")
    public int modifyPassword(@Valid ModifyPasswordForm form) {
        return systemUserService.modifyPassword(form);
    }
}
