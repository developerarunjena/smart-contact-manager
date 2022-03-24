package com.smartcontact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SmartContactMangerApplication extends SpringBootServletInitializer {


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SmartContactMangerApplication.class);
    }

    @Autowired
    private BCryptPasswordEncoder bryBCryptPasswordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SmartContactMangerApplication.class, args);
    }
}
