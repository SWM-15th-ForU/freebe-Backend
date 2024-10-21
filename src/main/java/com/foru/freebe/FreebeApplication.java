package com.foru.freebe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "com.foru.freebe")
@EnableJpaAuditing
public class FreebeApplication {
    public static void main(String[] args) {
        SpringApplication.run(FreebeApplication.class, args);
    }
}
