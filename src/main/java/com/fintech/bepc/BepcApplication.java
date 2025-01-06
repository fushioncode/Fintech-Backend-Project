package com.fintech.bepc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "FINTECH APIs", version = "1.0", description = "Fintech Backend Engineer Practical Challenge."))
public class BepcApplication {

	public static void main(String[] args) {
		SpringApplication.run(BepcApplication.class, args);
	}


}
