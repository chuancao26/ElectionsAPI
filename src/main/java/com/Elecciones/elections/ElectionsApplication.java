package com.Elecciones.elections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class ElectionsApplication {
	public static void main(String[] args) {
		SpringApplication.run(ElectionsApplication.class, args);
	}
}
