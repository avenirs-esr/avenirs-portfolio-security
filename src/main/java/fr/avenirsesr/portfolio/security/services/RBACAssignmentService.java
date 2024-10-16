package fr.avenirsesr.portfolio.security.services;
import java.util.List;

import fr.avenirsesr.portfolio.security.models.RBACAssignmentPK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.models.RBACAssignment;
import fr.avenirsesr.portfolio.security.repositories.RBACAssignmentRepository;



@Slf4j
@Service
public class RBACAssignmentService {
	
	@Autowired
	private RBACAssignmentRepository assignmentRepository;
	
	/**
	 * Gives all the assignments.
	 * @return All assignments.
	 */
	public List<RBACAssignment> getAllAssignments() {
		log.trace("getAllAssignments");
		return this.assignmentRepository.findAll();
	}
	
	
	/**
	 * Gives all the assignments associated to a principal.
	 * 
	 * @param specification The specification used to filter the assignments.
	 * @return The  filtered assignments.
	 */
	public List<RBACAssignment> getAllAssignmentsBySpecification(Specification<RBACAssignment> specification) {
		log.trace("getAllAssignmentsByPredicate, specification: {}", specification);
		return assignmentRepository.findAll(specification);
	}
	
	/**
	 * Creates an assignment.
	 * @param assignment The assignment to create.
	 * @return The saved assignment.
	 */
	public RBACAssignment createAssignment(RBACAssignment assignment) {
		log.trace("createAssignment, assignment: {}", assignment);
		return this.assignmentRepository.save(assignment);
	}

	/**
	 * Updates an assignment.
	 * @param assignment The assignment to update.
	 */
	public void updateAssignment(RBACAssignment assignment) {
		log.trace("updateAssignment, assignment: {}", assignment);
		this.assignmentRepository.save(assignment);
	}


	/**
	 * Deletes an assignment by id.
	 * @param assignmentId The id of the assignment to delete.
	 */
	public void deleteAssignment(RBACAssignmentPK assignmentId) {
		log.trace("deleteAssignment, assignmentId: {}", assignmentId);
		this.assignmentRepository.deleteById(assignmentId);
	}
}
