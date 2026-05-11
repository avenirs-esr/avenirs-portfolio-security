package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlNotFoundException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAssignment;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.RBACAssignmentService;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACAssignmentRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RBACAssignmentServiceImpl implements RBACAssignmentService {

  private final RBACAssignmentRepository assignmentRepository;

  @Override
  public List<RBACAssignment> getAllAssignments() {
    log.trace("getAllAssignments");
    return assignmentRepository.findAll();
  }

  @Override
  public Optional<RBACAssignment> getAssignmentById(UUID assignmentId) {
    log.trace("getAssignmentById, assignmentId: {}", assignmentId);
    return assignmentRepository.findById(assignmentId);
  }

  @Override
  public RBACAssignment createAssignment(RBACAssignment assignment) {
    log.trace("createAssignment, assignment: {}", assignment);
    return assignmentRepository.save(assignment);
  }

  @Override
  public RBACAssignment updateAssignment(RBACAssignment assignment) {
    log.trace("updateAssignment, assignment: {}", assignment);

    assignmentRepository
        .findById(assignment.id())
        .orElseThrow(() -> AccessControlNotFoundException.assignment(assignment.id()));

    return assignmentRepository.save(assignment);
  }

  @Override
  public void deleteAssignment(UUID assignmentId) {
    log.trace("deleteAssignment, assignmentId: {}", assignmentId);
    assignmentRepository.deleteById(assignmentId);
  }
}
