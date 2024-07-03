package fr.avenirsesr.portfolio.security.services;


import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import fr.avenirsesr.portfolio.security.models.RBACAction;
import fr.avenirsesr.portfolio.security.models.RBACPermission;
import jakarta.transaction.Transactional;
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:test-fixtures.sql")
@Transactional
class RBACActionServiceTest {
	
	@Autowired 
	private RBACActionService actionService;
	
	private static final String SHARE_READ_RESOURCE_NAME = "ACT_SHARE_READ_RESOURCE";
	private static final int SHARE_READ_RESOURCE_PERMS_COUNT=1;


	@Test
	void testGetAction() {
		
	}
	
	@Test
	void testGetActionByName() {
		RBACAction action = actionService.getAction(SHARE_READ_RESOURCE_NAME).orElseGet(null);
		
		assertNotNull(action);
		assertEquals(SHARE_READ_RESOURCE_NAME, action.getName());
		
		List<RBACPermission> permissions = action.getPermissions();
		
		assertEquals(SHARE_READ_RESOURCE_PERMS_COUNT, permissions.size());
		
		
		System.out.println("action " + action);
	}


	@Test
	void testGetAllActions() {
		actionService.getAllActions().forEach(a-> System.out.println("    Action:  " + a));
	}

}
