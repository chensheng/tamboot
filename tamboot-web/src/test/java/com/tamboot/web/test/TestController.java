package com.tamboot.web.test;

import com.tamboot.web.annotation.IgnoreResponseWrapper;
import com.tamboot.web.core.BusinessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/get")
    public TestDto get() {
        TestDto dto = new TestDto();
        dto.setUsername("Tam Boot");
        dto.setAge(1);
        return dto;
    }

    @GetMapping("/getNull")
    public TestDto getNull() {
        return null;
    }

    @GetMapping("/businessException")
    public TestDto businessException() {
        throw new BusinessException("business exception");
    }

    @GetMapping("/systemException")
    public TestDto systemException() {
        throw new NullPointerException();
    }

    @GetMapping("/queryByUsername")
    public TestDto queryByUsername(@Valid TestForm form) {
        TestDto dto = new TestDto();
        dto.setUsername(form.getUsername());
        dto.setAge(1);
        return dto;
    }

    @GetMapping("/dateFormat")
    public TestDateForm dateFormat(TestDateForm form) {
        return form;
    }

    @RequestMapping(path = "getTextPlain",  method = RequestMethod.GET, produces = {"text/plain; charset=UTF-8"})
    public String getTextPlain() {
        return "Tam Boot";
    }

    @GetMapping("/getIgnoreResponseWrapper")
    @IgnoreResponseWrapper
    public TestDto getIgnoreResponseWrapper() {
        TestDto testDto = new TestDto();
        testDto.setAge(1);
        testDto.setUsername("Tam Boot");
        return testDto;
    }
}
