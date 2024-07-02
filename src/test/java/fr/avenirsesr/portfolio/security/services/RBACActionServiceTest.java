package fr.avenirsesr.portfolio.security.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import jakarta.transaction.Transactional;
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:test-fixtures.sql")
@Transactional
class RBACActionServiceTest {
	
	@Autowired 
	private RBACActionService actionService;


	@Test
	void testGetAction() {
		
	}

	@Test
	void testGetAllActions() {
		actionService.getAllActions().forEach(a-> System.out.println("    Action:  " + a));
	}

}
