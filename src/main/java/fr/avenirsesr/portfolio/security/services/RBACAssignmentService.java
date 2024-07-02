package fr.avenirsesr.portfolio.security.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.models.RBACAssignment;
import fr.avenirsesr.portfolio.security.repositories.RBACAssignmentRepository;



@Service
public class RBACAssignmentService {
	
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(RBACAssignmentService.class);
	
	@Autowired
	private RBACAssignmentRepository assignmentRepository;
	
	/**
	 * Gives all the assignments.
	 * @return All assignments.
	 */
	public Iterable<RBACAssignment> getAllAssignments() {
		LOGGER.trace("getAllAssignments");
		return this.assignmentRepository.findAll();
	}
	
	
	/**
	 * Gives all the assignments associated to a principal.
	 * 
	 * @param id The id of the principal.
	 * @return The assignments for the principal.
	 */
	public Iterable<RBACAssignment> getAllAssignmentsByPredicate(Specification<RBACAssignment> specification) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("getAllAssignmentsByPredicate, specification: " + specification);
		}
		return assignmentRepository.findAll(specification);
	}
	
	/**
	 * Creates an assignment.
	 * @param assignment The assignment to create.
	 * @return The saved assignment.
	 */
	public RBACAssignment createAssignment(RBACAssignment assignment) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("createAssignment, assignment: " + assignment);
		}
		return this.assignmentRepository.save(assignment);
	}
	
	/**
	 * Deletes an assignment.
	 * @param assignment The assignment to delete.
	 */
	public void deleteAssignment(RBACAssignment assignment) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("deleteAssignment, assignment: " + assignment);
		}
		this.assignmentRepository.delete(assignment);
	}
	
	/**
	 * Deletes an assignment by id.
	 * @param assignmentId The id of the assignment to delete.
	 */
	public void deleteAssignmentById(Long assignmentId) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("deleteAssignment, assignmentId: " + assignmentId);
		}
		this.assignmentRepository.deleteById(assignmentId);
	}
		
	
	
}
