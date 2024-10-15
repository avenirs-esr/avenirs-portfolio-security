package fr.avenirsesr.portfolio.security.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import fr.avenirsesr.portfolio.security.models.Principal;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/test-fixtures-commons.sql")
@Transactional
class PrincipalServiceTest {

	@Value("${avenirs.test.principal.service.valid.user.login}")
	private  String validUser;

	@Value("${avenirs.test.principal.service.invalid.user.login}")
	private String invalidUser;

	@Value("${avenirs.test.principal.service.expected.principals}")
	private String[] expectedPrincipals;

	
	@Autowired 
	private PrincipalService principalService;
	
	@Test
	void testGetAllPrincipals() {
		List <Principal> actual  = principalService.getAllPrincipals();
		assertThat(actual).hasSize(actual.size());
		assertThat(actual.stream().map(Principal::getLogin)).containsExactly(expectedPrincipals);
	}

	@Test
	void testGetPrincipalByLogin() {
		Optional<Principal> response  = principalService.getPrincipalByLogin(validUser);
		assertFalse(response.isEmpty());
		assertEquals(response.get().getLogin(), validUser);
		response  = principalService.getPrincipalByLogin(invalidUser);
		assertTrue(response.isEmpty());
	}

}
