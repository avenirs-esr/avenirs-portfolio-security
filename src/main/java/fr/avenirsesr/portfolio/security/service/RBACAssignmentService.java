package fr.avenirsesr.portfolio.security.service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import fr.avenirsesr.portfolio.security.model.RBACAssignmentPK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.model.RBACAssignment;
import fr.avenirsesr.portfolio.security.repository.RBACAssignmentRepository;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Service
public class RBACAssignmentService {
	
	@Autowired
	private RBACAssignmentRepository assignmentRepository;
	
	/**
	 * Gives all the assignments.
	 * @return All assignments.
	 */
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
	public List<RBACAssignment> getAllAssignmentsBySpecification(Specification<RBACAssignment> specification) {
		log.trace("getAllAssignmentsByPredicate, specification: {}", specification);
		return assignmentRepository.findAll(specification);
	}

	/**
	 * Gives an assignment by its id.
	 *
	 * @param assignmentId The id of the assignment to retrieve.
	 * @return The  Optional with the assignment if found.
	 */
	@Transactional(readOnly = true)
	public Optional<RBACAssignment> getAssignmentsById(RBACAssignmentPK assignmentId) {
		log.trace("getAssignmentsById, assignmentId: {}", assignmentId);
		return assignmentRepository.findById(assignmentId);
	}
	
	/**
	 * Creates an assignment.
	 * @param assignment The assignment to create.
	 * @return The saved assignment.
	 */
	@Transactional
	public RBACAssignment createAssignment(RBACAssignment assignment) {
		log.trace("createAssignment, assignment: {}", assignment);
		if (assignment.getScope().getId()==null){
			assignment.getScope().setId(UUID.randomUUID());
		}
		if (assignment.getContext().getId()==null){
			assignment.getContext().setId(UUID.randomUUID());
		}
		return this.assignmentRepository.save(assignment);
	}



	/**
	 * Updates an assignment.
	 * @param assignment The assignment to update.
	 */
	@Transactional
	public void updateAssignment(RBACAssignment assignment) {
		log.trace("updateAssignment, assignment: {}", assignment);
		this.assignmentRepository.save(assignment);
	}


	/**
	 * Deletes an assignment by id.
	 * @param assignmentId The id of the assignment to delete.
	 */
	@Transactional
	public void deleteAssignment(RBACAssignmentPK assignmentId) {
		log.trace("deleteAssignment, assignmentId: {}", assignmentId);
		this.assignmentRepository.deleteById(assignmentId);
	}
}
