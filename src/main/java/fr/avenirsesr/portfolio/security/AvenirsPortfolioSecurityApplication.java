package fr.avenirsesr.portfolio.security;

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
