package fr.avenirsesr.portfolio.security.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import fr.avenirsesr.portfolio.security.models.RBACAssignment;
import fr.avenirsesr.portfolio.security.repositories.RBACAssignmentSpecification;
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
	

	@Test
	void testGetAllAssignments() {
		List<RBACAssignment> assignments = assignmentService.getAllAssignments();
		System.out.println("testGetAllAssignments, assignments: "+assignments);
		assertEquals(ALL_ASSIGNMENTS_SIZE, assignments.size(), "Total number of assignments");
	}
	
	@Test
	void testGetAllAssignmentsByPredicate() {
		Long[] l = new Long[] {(long)1, (long)2, (long)3, (long)4};
		System.out.println("testGetAllAssignments, assignments2 : "+assignmentService.getAllAssignments());
		List<RBACAssignment> assignments = 
				assignmentService.getAllAssignmentsByPredicate(RBACAssignmentSpecification.filterByPrincipalAndResources("deman", l));
		System.out.println("testGetAllAssignmentsByPredicate, assignments: " + assignments);
	}

}
