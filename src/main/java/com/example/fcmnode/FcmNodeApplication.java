package com.example.fcmnode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
public class FcmNodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FcmNodeApplication.class, args);
    }

}
