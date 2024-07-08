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
class AccessControlServiceTest {
	private final String USER_1="deman";
	private final String USER_2="gribonvald";
	private final Long  ACTION_SHARE_READ_ID=1L;
	private final Long  ACTION_SHARE_WRITE_ID=2L;
	private final Long  ACTION_DISPLAY_ID=3L;
	private final Long  ACTION_EDIT_ID=4L;
	private final Long  ACTION_DO_FEEDBACK_ID=5L;
	private final Long  ACTION_DELETE_ID=6L;
	private final Long RESOURCE_ID_1 = 1L;
	
	@Autowired 
	private AccessControlService accessControlService;
	
	
	
	
	@Test
	void testOwnerAccess() {
		assertTrue(accessControlService.hasAccess(USER_1, ACTION_SHARE_READ_ID, RESOURCE_ID_1));
		assertTrue(accessControlService.hasAccess(USER_1, ACTION_SHARE_WRITE_ID, RESOURCE_ID_1));
		assertTrue(accessControlService.hasAccess(USER_1, ACTION_DISPLAY_ID, RESOURCE_ID_1));
		assertTrue(accessControlService.hasAccess(USER_1, ACTION_EDIT_ID, RESOURCE_ID_1));
		assertTrue(accessControlService.hasAccess(USER_1, ACTION_DO_FEEDBACK_ID, RESOURCE_ID_1));
		assertTrue(accessControlService.hasAccess(USER_1, ACTION_DELETE_ID, RESOURCE_ID_1));
	}
	
	
//	@Test
//	void testCanShareWriteOnItsResourceAccess() {
//		boolean granted = accessControlService.hasAccess(USER_1, ACTION_SHARE_WRITE_ID, RESOURCE_ID_1);
//		assertTrue(granted);
//	}
	
	

}
