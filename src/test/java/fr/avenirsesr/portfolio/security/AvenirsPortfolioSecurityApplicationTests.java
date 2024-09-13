package fr.avenirsesr.portfolio.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fr.avenirsesr.portfolio.security.services.AuthenticationService;

@SpringBootTest
class AvenirsPortfolioSecurityApplicationTests {
	@Autowired
	private AuthenticationService authenticationService;
	
	
	
	@Test
	void testAccessToken() throws Exception {
		authenticationService.getAccessToken("deman", "Azerty123");
		
	}

}
