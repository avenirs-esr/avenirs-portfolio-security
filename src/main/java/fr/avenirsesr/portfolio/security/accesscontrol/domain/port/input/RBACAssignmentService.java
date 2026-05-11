package fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAssignment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RBACAssignmentService {

  List<RBACAssignment> getAllAssignments();

  Optional<RBACAssignment> getAssignmentById(UUID assignmentId);

  RBACAssignment createAssignment(RBACAssignment assignment);

  RBACAssignment updateAssignment(RBACAssignment assignment);

  void deleteAssignment(UUID assignmentId);
}
