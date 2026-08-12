package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.EPermission;
import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.RBACCatalogSynchronizationException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACPermission;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACRole;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACPermissionRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACRoleRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
    PermissionSyncSummary permissionSummary = new PermissionSyncSummary();
    for (EPermission permission : EPermission.values()) {
      permissionSummary.record(seedPermission(permission));
    }
    log.info(
        "RBAC catalog synchronized: {} permissions created, {} renamed, {} updated, {} unchanged",
        permissionSummary.created,
        permissionSummary.renamed,
        permissionSummary.updated,
        permissionSummary.unchanged);

    RoleSyncSummary roleSummary = new RoleSyncSummary();
    for (ERole role : ERole.values()) {
      seedRole(role, roleSummary);
    }
    log.info(
        "RBAC roles synchronized: {} roles created, {} relations added, {} relations removed",
        roleSummary.rolesCreated.get(),
        roleSummary.relationsAdded.get(),
        roleSummary.relationsRemoved.get());
  }

  private PermissionSyncOutcome seedPermission(EPermission permission) {
    Optional<RBACPermission> byAuthority = permissionRepository.findByName(permission.authority());

    boolean legacyDiffersFromAuthority =
        !permission.authority().equals(permission.legacyAuthority());
    Optional<RBACPermission> byLegacyAuthority =
        legacyDiffersFromAuthority
            ? permissionRepository.findByName(permission.legacyAuthority())
            : Optional.empty();

    if (byAuthority.isPresent()
        && byLegacyAuthority.isPresent()
        && !byAuthority.get().id().equals(byLegacyAuthority.get().id())) {
      throw RBACCatalogSynchronizationException.splitAcrossTwoRows(
          permission, byAuthority.get(), byLegacyAuthority.get());
    }

    if (byAuthority.isPresent()) {
      RBACPermission existing = byAuthority.get();
      if (existing.description().equals(permission.description())) {
        return PermissionSyncOutcome.UNCHANGED;
      }
      permissionRepository.save(
          new RBACPermission(existing.id(), permission.authority(), permission.description()));
      return PermissionSyncOutcome.UPDATED;
    }

    if (byLegacyAuthority.isPresent()) {
      RBACPermission existing = byLegacyAuthority.get();
      try {
        permissionRepository.save(
            new RBACPermission(existing.id(), permission.authority(), permission.description()));
      } catch (DataIntegrityViolationException e) {
        throw RBACCatalogSynchronizationException.authorityCollision(
            permission, existing.id(), existing.name());
      }
      log.debug(
          "Renamed permission id={} from legacy name '{}' to authority '{}'",
          existing.id(),
          permission.legacyAuthority(),
          permission.authority());
      return PermissionSyncOutcome.RENAMED;
    }

    permissionRepository.save(
        new RBACPermission(null, permission.authority(), permission.description()));
    return PermissionSyncOutcome.CREATED;
  }

  private void seedRole(ERole role, RoleSyncSummary summary) {
    Set<RBACPermission> permissions =
        role.permissions().stream()
            .map(
                permission ->
                    permissionRepository
                        .findByName(permission.authority())
                        .orElseThrow(
                            () ->
                                RBACCatalogSynchronizationException.permissionNotFound(
                                    role.name(), permission)))
            .collect(Collectors.toSet());

    Optional<RBACRole> existing = roleRepository.findByName(role.name());

    Set<UUID> previousPermissionIds =
        existing
            .map(r -> r.permissions().stream().map(RBACPermission::id).collect(Collectors.toSet()))
            .orElseGet(HashSet::new);
    Set<UUID> nextPermissionIds =
        permissions.stream().map(RBACPermission::id).collect(Collectors.toSet());

    if (existing.isEmpty()) {
      summary.rolesCreated.incrementAndGet();
      // Persisting a brand-new role with its permissions in one call would cascade
      // CascadeType.PERSIST onto already-existing (detached) permission entities and fail:
      // create it bare first, so the follow-up save merges instead of persisting.
      existing =
          Optional.of(
              roleRepository.save(new RBACRole(null, role.name(), role.description(), Set.of())));
    }

    summary.relationsAdded.addAndGet(countMissing(nextPermissionIds, previousPermissionIds));
    summary.relationsRemoved.addAndGet(countMissing(previousPermissionIds, nextPermissionIds));

    roleRepository.save(
        new RBACRole(existing.get().id(), role.name(), role.description(), permissions));
  }

  private static int countMissing(Set<UUID> target, Set<UUID> reference) {
    return (int) target.stream().filter(id -> !reference.contains(id)).count();
  }

  private enum PermissionSyncOutcome {
    CREATED,
    RENAMED,
    UPDATED,
    UNCHANGED
  }

  private static final class PermissionSyncSummary {
    private int created;
    private int renamed;
    private int updated;
    private int unchanged;

    private void record(PermissionSyncOutcome outcome) {
      switch (outcome) {
        case CREATED -> created++;
        case RENAMED -> renamed++;
        case UPDATED -> updated++;
        case UNCHANGED -> unchanged++;
      }
    }
  }

  private static final class RoleSyncSummary {
    private final AtomicInteger rolesCreated = new AtomicInteger();
    private final AtomicInteger relationsAdded = new AtomicInteger();
    private final AtomicInteger relationsRemoved = new AtomicInteger();
  }
}
