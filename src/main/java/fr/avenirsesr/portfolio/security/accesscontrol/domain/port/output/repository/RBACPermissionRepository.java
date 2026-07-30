package fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACPermission;
import java.util.List;
import java.util.Optional;

public interface RBACPermissionRepository {

  Optional<RBACPermission> findByName(String name);

  List<RBACPermission> findAll();

  RBACPermission save(RBACPermission permission);
}
