package fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RBACActionRepository {

  Optional<RBACAction> findById(UUID actionId);

  Optional<RBACAction> findByName(String name);

  List<RBACAction> findAll();
}
