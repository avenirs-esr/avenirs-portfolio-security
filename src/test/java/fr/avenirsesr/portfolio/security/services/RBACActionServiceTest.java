package fr.avenirsesr.portfolio.security.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import fr.avenirsesr.portfolio.security.models.RBACAction;
import jakarta.transaction.Transactional;


@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/test-fixtures.sql")
@Transactional
class RBACActionServiceTest {
	
	private static final Long SHARE_READ_RESOURCE_ID = 1L;
	private static final String SHARE_READ_RESOURCE_NAME = "ACT_SHARE_READ_RESOURCE";
	private static final String[] SHARE_READ_RESOURCE_PERMS= {"PERM_SEE", "PERM_SHARE"};
	
	private static final Long DO_FEEDBACK_ID = 5L;
	private static final String DO_FEEDBACK_NAME = "ACT_DO_FEEDBACK";
	private static final String[] DO_FEEDBACK_PERMS={"PERM_SEE", "PERM_READ", "PERM_COMMENT"};

	private static final Long DELETE_ID = 6L;
	private static final String DELETE_NAME = "ACT_DELETE";
	private static final String[] DELETE_PERMS={"PERM_SEE", "PERM_READ", "PERM_DELETE"};
	
	private final static String[] EXPECTED_ACTION_NAMES = {
			"ACT_SHARE_READ_RESOURCE",
			"ACT_SHARE_WRITE_RESOURCE",
			"ACT_DISPLAY",
			"ACT_EDIT",
			"ACT_DO_FEEDBACK",
			"ACT_DELETE"
			};
	
	@Autowired 
	private RBACActionService actionService;
		
	
	@Test
	void testGetActionByNameShareRead() {
		RBACAction actual = actionService.getActionByName(SHARE_READ_RESOURCE_NAME).get();
		assertEquals(SHARE_READ_RESOURCE_NAME, actual.getName());
		assertThat(actual.getPermissions()).hasSize(SHARE_READ_RESOURCE_PERMS.length);
		assertThat(actual.getPermissions().stream().map(p->p.getName())).contains(SHARE_READ_RESOURCE_PERMS);
	}
	
	@Test
	void testGetActionByNameDoFeedback() {
		RBACAction actual = actionService.getActionByName(DO_FEEDBACK_NAME).get();
		assertEquals(DO_FEEDBACK_NAME, actual.getName());
		assertThat(actual.getPermissions()).hasSize(DO_FEEDBACK_PERMS.length);
		assertThat(actual.getPermissions().stream().map(p->p.getName())).contains(DO_FEEDBACK_PERMS);
	}

	
	@Test
	void testGetActionByNameDelete() {
		RBACAction actual = actionService.getActionByName(DELETE_NAME).get();
		assertEquals(DELETE_NAME, actual.getName());
		assertThat(actual.getPermissions()).hasSize(DELETE_PERMS.length);
		assertThat(actual.getPermissions().stream().map(p->p.getName())).contains(DELETE_PERMS);
	}

	
	@Test
	void testGetActionById() {
		RBACAction actual = actionService.getActionById(SHARE_READ_RESOURCE_ID).get();
		assertEquals(SHARE_READ_RESOURCE_ID, actual.getId());
		assertEquals(SHARE_READ_RESOURCE_NAME, actual.getName());
		
		actual = actionService.getActionById(DO_FEEDBACK_ID).get();
		assertEquals(DO_FEEDBACK_ID, actual.getId());
		assertEquals(DO_FEEDBACK_NAME, actual.getName());
		
		actual = actionService.getActionById(DELETE_ID).get();
		assertEquals(DELETE_ID, actual.getId());
		assertEquals(DELETE_NAME, actual.getName());
	}


	@Test
	void testGetAllActions() {
		List<RBACAction> actual = actionService.getAllActions();
		assertThat(actual).hasSize(EXPECTED_ACTION_NAMES.length);
		assertThat(actual.stream().map(a->a.getName())).contains(EXPECTED_ACTION_NAMES);
	}

}
