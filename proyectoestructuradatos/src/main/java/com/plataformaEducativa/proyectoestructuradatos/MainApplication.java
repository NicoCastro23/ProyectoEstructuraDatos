package com.plataformaEducativa.proyectoestructuradatos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.plataformaEducativa.proyectoestructuradatos")
@EntityScan(basePackages = "com.plataformaEducativa.proyectoestructuradatos.entity")
@EnableJpaRepositories(basePackages = "com.plataformaEducativa.proyectoestructuradatos.repository")
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}
}
