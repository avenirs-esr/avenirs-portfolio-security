package fr.avenirsesr.portfolio.security.services;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import fr.avenirsesr.portfolio.security.models.RBACScope;
import fr.avenirsesr.portfolio.security.repositories.RBACScopeSpecification;
import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:test-fixtures.sql")
@Transactional
class RBACScopeServiceTest {
	@Autowired 
	private RBACScopeService scopeService;
	
	@Test
	void testGetAllScopesByPredicate() {
		List<RBACScope> scopes = scopeService.getAllScopesByPredicate(RBACScopeSpecification.filterByResources((long)1, (long)2));
		System.out.println("scopes: " + scopes);
	}

}
