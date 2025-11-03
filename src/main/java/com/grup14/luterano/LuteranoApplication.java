package com.grup14.luterano;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LuteranoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LuteranoApplication.class, args);
    }

}
