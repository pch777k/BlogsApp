package com.pch777.blogs;

import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.AllArgsConstructor;

@EnableJpaAuditing
@AllArgsConstructor
@SpringBootApplication
public class BlogsAppApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(BlogsAppApplication.class, args);
	}
	
	@Bean
	public Random random() {
		return new Random();
	}

}
