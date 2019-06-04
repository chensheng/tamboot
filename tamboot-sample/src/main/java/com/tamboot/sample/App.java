package com.tamboot.sample;

import com.tamboot.sample.security.RoleBasedPermissionInitializer;
import com.tamboot.sample.utils.ApplicationContextHolder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude ={QuartzAutoConfiguration.class})
@EnableTransactionManagement
@MapperScan("com.tamboot.sample.mapper")
public class App {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.addListeners(new RoleBasedPermissionInitializer());
        app.run();
    }

    @Bean
    public ApplicationContextHolder applicationContextHolder() {
       return new ApplicationContextHolder();
    }
}
