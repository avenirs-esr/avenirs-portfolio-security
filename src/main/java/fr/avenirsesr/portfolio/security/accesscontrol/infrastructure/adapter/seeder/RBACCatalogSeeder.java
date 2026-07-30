package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACPermission;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACRole;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.enums.EPermission;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.enums.ERole;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACPermissionRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACRoleRepository;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RBACCatalogSeeder {

  private final RBACPermissionRepository permissionRepository;
  private final RBACRoleRepository roleRepository;

  @Transactional
  public void seed() {
    for (EPermission permission : EPermission.values()) {
      seedPermission(permission);
    }
    for (ERole role : ERole.values()) {
      seedRole(role);
    }
    log.info("✔ RBAC catalog (roles and permissions) seeded");
  }

  private void seedPermission(EPermission permission) {
    RBACPermission existing = permissionRepository.findByName(permission.name()).orElse(null);

    if (existing != null && existing.description().equals(permission.description())) {
      return;
    }

    permissionRepository.save(
        new RBACPermission(
            existing == null ? null : existing.id(), permission.name(), permission.description()));
  }

  private void seedRole(ERole role) {
    Set<RBACPermission> permissions =
        role.permissions().stream()
            .map(permission -> permissionRepository.findByName(permission.name()).orElseThrow())
            .collect(Collectors.toSet());

    RBACRole existing = roleRepository.findByName(role.name()).orElse(null);

    if (existing == null) {
      // Persisting a brand-new role with its permissions in one call would cascade
      // CascadeType.PERSIST onto already-existing (detached) permission entities and fail:
      // create it bare first, so the follow-up save merges instead of persisting.
      existing = roleRepository.save(new RBACRole(null, role.name(), role.description(), Set.of()));
    }

    roleRepository.save(new RBACRole(existing.id(), role.name(), role.description(), permissions));
  }
}
