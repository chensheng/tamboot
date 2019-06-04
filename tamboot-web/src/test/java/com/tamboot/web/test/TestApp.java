package com.tamboot.web.test;

import com.tamboot.web.config.TambootWebAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({TambootWebAutoConfiguration.class})
public class TestApp {
}
