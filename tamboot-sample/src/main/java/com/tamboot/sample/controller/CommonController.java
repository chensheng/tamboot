package com.tamboot.sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/common")
public class CommonController {

    @GetMapping("/provinces")
    public List<String> provinces() {
        List<String> provinces = new ArrayList<String>();
        provinces.add("广东省");
        provinces.add("福建省");
        provinces.add("江苏省");
        provinces.add("浙江省");
        return provinces;
    }
}
