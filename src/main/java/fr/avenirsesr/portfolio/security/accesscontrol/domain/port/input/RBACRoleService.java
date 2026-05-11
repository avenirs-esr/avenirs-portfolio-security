package fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RBACRoleService {

  Optional<RBACRole> getRoleById(UUID roleId);

  Optional<RBACRole> getRoleByName(String roleName);

  List<RBACRole> getAllRoles();

  RBACRole createRole(RBACRole role);

  RBACRole updateRole(RBACRole role);

  void deleteRole(UUID roleId);

  List<RBACRole> getRolesByPrincipalLogin(String login);
}
