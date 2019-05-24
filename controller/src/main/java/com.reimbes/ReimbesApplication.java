package com.reimbes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReimbesApplication {

	public static void main(String[] args) {
		nu.pattern.OpenCV.loadShared();
		SpringApplication.run(ReimbesApplication.class, args);
	}

}

