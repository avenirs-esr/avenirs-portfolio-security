package fr.avenirsesr.portfolio.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import fr.avenirsesr.portfolio.security.services.AuthenticationService;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts="classpath:db/test-fixtures-commons.sql")
@Transactional
class AvenirsPortfolioSecurityApplicationTests {
	@Autowired
	private AuthenticationService authenticationService;
	
	
	
	@Test
	void testAccessToken() throws Exception {
		authenticationService.getAccessToken("deman", "Azerty123");
		
	}

}
