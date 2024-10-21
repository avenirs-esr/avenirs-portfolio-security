package fr.avenirsesr.portfolio.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import jakarta.transaction.Transactional;


/**
 * Avenirs Portfolio security module.
 */

@Slf4j
@SpringBootApplication
@EnableEncryptableProperties
@OpenAPIDefinition(
		info = @Info(
				title = "API Documentation",
				version = "v1",
				description = "API Documentation for Avenirs Portfolio Security"
		)
)
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.APIKEY,
		scheme = "bearer",
		bearerFormat = "JWT",
		paramName = "x-authorization",
		in = SecuritySchemeIn.HEADER
)
public class AvenirsPortfolioSecurityApplication implements CommandLineRunner {
	
	public static void main(String[] args) {
		log.info("Starting Avenirs Portfolio Security Module");
		/* ApplicationContext app = */SpringApplication.run(AvenirsPortfolioSecurityApplication.class, args);
	}

	@Transactional
	@Override
	public void run(String... args) throws Exception {
		log.info("AvenirsPortfolioSecurityApplication running");
	}
}
