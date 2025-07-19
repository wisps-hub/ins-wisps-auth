package com.wisps.auth;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.wisps.auth"})
@EnableDubbo
public class WispsAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(WispsAuthApplication.class, args);
    }

}