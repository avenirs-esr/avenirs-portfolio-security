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
@Sql(scripts = "classpath:db/test-fixtures.sql")
@Transactional
class AccessControlServiceTest {
	private final String USER_1="deman";
	private final String USER_2="gribonvald";
	private final String USER_3="paterson";
	private final Long  ACTION_SHARE_READ_ID=1L;
	private final Long  ACTION_SHARE_WRITE_ID=2L;
	private final Long  ACTION_DISPLAY_ID=3L;
	private final Long  ACTION_EDIT_ID=4L;
	private final Long  ACTION_DO_FEEDBACK_ID=5L;
	private final Long  ACTION_DELETE_ID=6L;
	private final Long RESOURCE_ID_1 = 1L;
	private final Long RESOURCE_ID_2 = 2L;
	private final Long RESOURCE_ID_3 = 3L;
	
	@Autowired 
	private AccessControlService accessControlService;
	
	
	
	
	@Test
	void testOwnerAccess() {
		
		// User 1 is owner of resource 1.
		assertTrue(accessControlService.hasAccess(USER_1, ACTION_SHARE_READ_ID, RESOURCE_ID_1));
		assertTrue(accessControlService.hasAccess(USER_1, ACTION_SHARE_WRITE_ID, RESOURCE_ID_1));
		assertTrue(accessControlService.hasAccess(USER_1, ACTION_DISPLAY_ID, RESOURCE_ID_1));
		assertTrue(accessControlService.hasAccess(USER_1, ACTION_EDIT_ID, RESOURCE_ID_1));
		assertTrue(accessControlService.hasAccess(USER_1, ACTION_DO_FEEDBACK_ID, RESOURCE_ID_1));
		assertTrue(accessControlService.hasAccess(USER_1, ACTION_DELETE_ID, RESOURCE_ID_1));

		// User 2 is owner of resource 2.
		assertTrue(accessControlService.hasAccess(USER_2, ACTION_SHARE_READ_ID, RESOURCE_ID_2));
		assertTrue(accessControlService.hasAccess(USER_2, ACTION_SHARE_WRITE_ID, RESOURCE_ID_2));
		assertTrue(accessControlService.hasAccess(USER_2, ACTION_DISPLAY_ID, RESOURCE_ID_2));
		assertTrue(accessControlService.hasAccess(USER_2, ACTION_EDIT_ID, RESOURCE_ID_2));
		assertTrue(accessControlService.hasAccess(USER_2, ACTION_DO_FEEDBACK_ID, RESOURCE_ID_2));
		assertTrue(accessControlService.hasAccess(USER_2, ACTION_DELETE_ID, RESOURCE_ID_2));

		//Checks the independence of ownerships.
		assertFalse(accessControlService.hasAccess(USER_1, ACTION_SHARE_READ_ID, RESOURCE_ID_2));
		assertFalse(accessControlService.hasAccess(USER_1, ACTION_SHARE_WRITE_ID, RESOURCE_ID_2));
		assertFalse(accessControlService.hasAccess(USER_1, ACTION_DISPLAY_ID, RESOURCE_ID_2));
		assertFalse(accessControlService.hasAccess(USER_1, ACTION_EDIT_ID, RESOURCE_ID_2));
		assertFalse(accessControlService.hasAccess(USER_1, ACTION_DO_FEEDBACK_ID, RESOURCE_ID_2));
		assertFalse(accessControlService.hasAccess(USER_1, ACTION_DELETE_ID, RESOURCE_ID_2));
		
		assertFalse(accessControlService.hasAccess(USER_2, ACTION_SHARE_READ_ID, RESOURCE_ID_1));
		assertFalse(accessControlService.hasAccess(USER_2, ACTION_SHARE_WRITE_ID, RESOURCE_ID_1));
		assertFalse(accessControlService.hasAccess(USER_2, ACTION_DISPLAY_ID, RESOURCE_ID_1));
		assertFalse(accessControlService.hasAccess(USER_2, ACTION_EDIT_ID, RESOURCE_ID_1));
		assertFalse(accessControlService.hasAccess(USER_2, ACTION_DO_FEEDBACK_ID, RESOURCE_ID_1));
		assertFalse(accessControlService.hasAccess(USER_2, ACTION_DELETE_ID, RESOURCE_ID_1));
	}
	
	
	@Test
	void testPairAccess() {
		assertFalse(accessControlService.hasAccess(USER_2, ACTION_SHARE_READ_ID, RESOURCE_ID_3));
		assertFalse(accessControlService.hasAccess(USER_1, ACTION_SHARE_WRITE_ID, RESOURCE_ID_3));
		assertTrue(accessControlService.hasAccess(USER_1, ACTION_DISPLAY_ID, RESOURCE_ID_3));
		assertFalse(accessControlService.hasAccess(USER_1, ACTION_EDIT_ID, RESOURCE_ID_3));
		assertTrue(accessControlService.hasAccess(USER_1, ACTION_DO_FEEDBACK_ID, RESOURCE_ID_3));
		assertFalse(accessControlService.hasAccess(USER_1, ACTION_DELETE_ID, RESOURCE_ID_3));
		
	}
	
	@Test
	void testNoPermAccess() {
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_SHARE_READ_ID, RESOURCE_ID_1));
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_SHARE_WRITE_ID, RESOURCE_ID_1));
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_DISPLAY_ID, RESOURCE_ID_1));
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_EDIT_ID, RESOURCE_ID_1));
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_DO_FEEDBACK_ID, RESOURCE_ID_1));
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_DELETE_ID, RESOURCE_ID_1));
		
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_SHARE_READ_ID, RESOURCE_ID_2));
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_SHARE_WRITE_ID, RESOURCE_ID_2));
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_DISPLAY_ID, RESOURCE_ID_2));
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_EDIT_ID, RESOURCE_ID_2));
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_DO_FEEDBACK_ID, RESOURCE_ID_2));
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_DELETE_ID, RESOURCE_ID_2));
		
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_SHARE_READ_ID, RESOURCE_ID_3));
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_SHARE_WRITE_ID, RESOURCE_ID_3));
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_DISPLAY_ID, RESOURCE_ID_3));
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_EDIT_ID, RESOURCE_ID_3));
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_DO_FEEDBACK_ID, RESOURCE_ID_3));
		assertFalse(accessControlService.hasAccess(USER_3, ACTION_DELETE_ID, RESOURCE_ID_3));
	}
}
