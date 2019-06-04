package com.tamboot.sample.controller;

import com.tamboot.sample.service.SystemUserService;
import com.tamboot.sample.form.ResetPasswordForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    private SystemUserService systemUserService;

    @PostMapping("/resetPassword")
    public int resetPassword(@Valid ResetPasswordForm form) {
        return systemUserService.resetPassword(form);
    }
}
