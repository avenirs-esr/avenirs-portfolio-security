package fr.avenirsesr.portfolio.security.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import fr.avenirsesr.portfolio.security.repositories.RBACActionRouteSpecificationHelper;
import jakarta.transaction.Transactional;

@Disabled
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/test-fixtures-commons.sql")
@Transactional
class RBACActionRouteServiceTest {

	private static final String ROUTE_1 = "/";
	private static final String METH_1 = "get";
	private static final String ACTION_NAME_1 = "ACT_DISPLAY";
	
	private static final String ROUTE_2 = "/edit";
	private static final String METH_2 = "post";
	private static final String ACTION_NAME_2 = "ACT_EDIT";

	@Autowired
	private RBACActionRouteService actionRouteService;

	@Test
	void testGetActionRouteByilterByURIAndMethodPredicateF() {
		var route = actionRouteService.getAllActionRoutesBySpecification(RBACActionRouteSpecificationHelper.filterByURIAndMethod(ROUTE_1, METH_1)).get();
		assertEquals(ROUTE_1, route.getUri());
		assertEquals(METH_1, route.getMethod());
		assertEquals(ACTION_NAME_1, route.getAction().getName());
		
		route = actionRouteService.getAllActionRoutesBySpecification(RBACActionRouteSpecificationHelper.filterByURIAndMethod(ROUTE_2, METH_2)).get();
		assertEquals(ROUTE_2, route.getUri());
		assertEquals(METH_2, route.getMethod());
		assertEquals(ACTION_NAME_2, route.getAction().getName());
	}

}
