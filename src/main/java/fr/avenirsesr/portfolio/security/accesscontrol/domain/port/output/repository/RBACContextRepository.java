package fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACContext;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RBACContextRepository {

  List<RBACContext> findAll();

  Optional<RBACContext> findById(UUID contextId);

  RBACContext save(RBACContext context);

  void deleteById(UUID contextId);
}
