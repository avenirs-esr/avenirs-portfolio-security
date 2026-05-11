package fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACResource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RBACResourceRepository {

  List<RBACResource> findAllByIds(List<UUID> ids);

  Optional<RBACResource> findById(UUID resourceId);

  List<RBACResource> findAll();

  RBACResource save(RBACResource resource);

  void deleteById(UUID resourceId);
}
