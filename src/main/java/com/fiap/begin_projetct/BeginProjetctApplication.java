package com.fiap.begin_projetct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.fiap.begin_projetct", "com.careplus"})
public class BeginProjetctApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeginProjetctApplication.class, args);
	}

}
