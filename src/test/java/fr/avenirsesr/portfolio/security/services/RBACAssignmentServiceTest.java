package fr.avenirsesr.portfolio.security.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import fr.avenirsesr.portfolio.security.models.RBACAssignment;
import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:test-fixtures.sql")
@Transactional
class RBACAssignmentServiceTest {

	@Autowired 
	private RBACAssignmentService assignmentService;
	
	/** Number of assignments in fixtures. */
	private final static int ALL_ASSIGNMENTS_SIZE=4;
	

	

	 @Transactional
	@Test
	void testGetAllAssignments() {
		Iterable<RBACAssignment> assignmentsIt = assignmentService.getAllAssignments();
		List<RBACAssignment> assignments = new ArrayList<>();
		assignmentsIt.forEach(assignments::add);
		assertEquals(ALL_ASSIGNMENTS_SIZE, assignments.size(), "Total number of assignments");
	}

}
