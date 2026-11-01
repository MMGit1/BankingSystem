package ru.merkushev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RatesServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RatesServiceApplication.class, args);
    }
}