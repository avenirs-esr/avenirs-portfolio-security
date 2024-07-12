package fr.avenirsesr.portfolio.security.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import fr.avenirsesr.portfolio.security.models.Principal;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:test-fixtures.sql")
@Transactional
class PrincipalServiceTest {
	
	private final static String VALID_USER="deman"; 
	private final static String INVALID_USER="deman2"; 
	private final static String[] EXPECTED_PRINCIPALS= {"deman", "gribonvald", "dugat", "patterson"};

	
	@Autowired 
	private PrincipalService principalService;
	
	@Test
	void testGetAllPrincipals() {
		List <Principal> actual  = principalService.getAllPrincipals();
		System.out.println("actual:" + actual);
		assertThat(actual).hasSize(actual.size());
		assertThat(actual.stream().map(p -> p.getLogin())).contains(EXPECTED_PRINCIPALS);
	}

	@Test
	void testGetPrincipalByLogin() {
		Optional<Principal> validUser  = principalService.getPrincipalByLogin(VALID_USER);
		assertFalse(validUser.isEmpty());
		assertEquals(validUser.get().getLogin(), VALID_USER);
		System.out.println("Valid User: " + validUser);
		Optional<Principal> invalidUser  = principalService.getPrincipalByLogin(INVALID_USER);
		assertTrue(invalidUser.isEmpty());
	}

}
