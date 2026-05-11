package fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAssignment;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACContext;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RBACAssignmentRepository {

  RBACAssignment save(RBACAssignment assignment);

  void deleteById(UUID assignmentId);

  List<RBACAssignment> findByPrincipalContextAndResource(
      String login, RBACContext context, UUID resourceId);

  List<RBACAssignment> findAll();

  Optional<RBACAssignment> findById(UUID assignmentId);

  List<RBACAssignment> findByPrincipal(String login);
}
