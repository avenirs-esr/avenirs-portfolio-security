package fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RBACRoleRepository {

  Optional<RBACRole> findById(UUID roleId);

  Optional<RBACRole> findByName(String roleName);

  List<RBACRole> findAll();

  RBACRole save(RBACRole role);

  void deleteById(UUID roleId);
}
