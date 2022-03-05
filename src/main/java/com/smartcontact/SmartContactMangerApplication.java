package com.smartcontact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SmartContactMangerApplication{


        @Autowired
        private BCryptPasswordEncoder bryBCryptPasswordEncoder;

        public static void main(String[] args) {
            SpringApplication.run(SmartContactMangerApplication.class, args);
        }
}
