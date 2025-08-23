package com.grup14.luterano;

import com.grup14.luterano.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LuteranoApplication {

	public static void main(String[] args) {
		SpringApplication.run(LuteranoApplication.class, args);}

}
