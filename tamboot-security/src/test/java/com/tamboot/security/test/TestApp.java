package com.tamboot.security.test;

import com.tamboot.security.config.TambootSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({TambootSecurityAutoConfiguration.class, SecurityAutoConfiguration.class})
public class TestApp {
}
