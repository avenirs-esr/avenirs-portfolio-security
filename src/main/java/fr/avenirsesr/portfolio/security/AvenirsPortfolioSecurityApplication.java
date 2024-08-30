package fr.avenirsesr.portfolio.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.transaction.Transactional;

/**
 * Avenirs Portfolio security module.
 */
@SpringBootApplication
public class AvenirsPortfolioSecurityApplication implements CommandLineRunner {
	
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(AvenirsPortfolioSecurityApplication.class);

	public static void main(String[] args) {
		LOGGER.info("Starting Avenirs Portfolio Securiy Module");
		/* ApplicationContext app = */SpringApplication.run(AvenirsPortfolioSecurityApplication.class, args);
	}

	@Transactional
	@Override
	public void run(String... args) throws Exception {
		LOGGER.info("AvenirsPortfolioSecurityApplication running");
	}
}
