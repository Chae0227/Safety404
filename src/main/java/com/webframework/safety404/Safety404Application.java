package com.webframework.safety404;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.webframework.safety404")
public class Safety404Application {

    public static void main(String[] args) {
        SpringApplication.run(Safety404Application.class, args);
    }
}