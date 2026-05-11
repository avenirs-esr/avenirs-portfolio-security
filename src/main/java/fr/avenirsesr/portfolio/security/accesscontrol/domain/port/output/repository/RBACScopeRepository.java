package fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACScope;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RBACScopeRepository {

  Optional<RBACScope> findById(UUID scopeId);

  Optional<RBACScope> findByName(String scopeName);

  List<RBACScope> findAll();

  RBACScope save(RBACScope scope);

  void deleteById(UUID scopeId);
}
