package fr.avenirsesr.portfolio.security.service;

import fr.avenirsesr.portfolio.security.model.RBACActionRoute;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import fr.avenirsesr.portfolio.security.repository.RBACActionRouteSpecificationHelper;
import jakarta.transaction.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/test-fixtures-commons.sql")
@Transactional
class RBACActionRouteServiceTest {

	@Value("${avenirs.test.action.route.service.route}")
	private String route;

	@Value("${avenirs.test.action.route.service.method}")
	private String method;

	@Value("${avenirs.test.action.route.service.action}")
	private String action;


	@Autowired
	private RBACActionRouteService actionRouteService;

	@Test
	void testGetActionRouteByFilterByURIAndMethodPredicateF() {
		Optional<RBACActionRoute> response = actionRouteService.getAllActionRoutesBySpecification(RBACActionRouteSpecificationHelper.filterByURIAndMethod(route,  method));
		assertFalse(response.isEmpty());
		RBACActionRoute actionRoute = response.get();
		assertEquals(route, actionRoute.getUri());
		assertTrue(HttpMethod.POST.name().equalsIgnoreCase(actionRoute.getMethod()));
	}

}
