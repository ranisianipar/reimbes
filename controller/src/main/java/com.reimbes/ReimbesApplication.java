package com.reimbes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class ReimbesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReimbesApplication.class, args);
	}

}

