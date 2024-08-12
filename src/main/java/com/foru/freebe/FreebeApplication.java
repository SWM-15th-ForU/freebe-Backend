package com.foru.freebe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.foru.freebe")
public class FreebeApplication {
    public static void main(String[] args) {
        SpringApplication.run(FreebeApplication.class, args);
    }
}
