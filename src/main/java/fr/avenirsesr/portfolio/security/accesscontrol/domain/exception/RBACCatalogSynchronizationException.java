package fr.avenirsesr.portfolio.security.accesscontrol.domain.exception;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACPermission;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.enums.EPermission;
import java.util.UUID;

public class RBACCatalogSynchronizationException extends RuntimeException {

  private RBACCatalogSynchronizationException(String message) {
    super(message);
  }

  public static RBACCatalogSynchronizationException splitAcrossTwoRows(
      EPermission permission, RBACPermission byNewAuthority, RBACPermission byLegacyAuthority) {
    String message =
        ("Permission %s is split across two rows: id=%s name='%s' (current authority) and"
                + " id=%s name='%s' (legacy authority). Merge or remove one of them manually"
                + " before re-running the synchronization.")
            .formatted(
                permission.name(),
                byNewAuthority.id(),
                byNewAuthority.name(),
                byLegacyAuthority.id(),
                byLegacyAuthority.name());
    return new RBACCatalogSynchronizationException(message);
  }

  public static RBACCatalogSynchronizationException authorityCollision(
      EPermission permission, UUID conflictingId, String conflictingName) {
    String message =
        ("Cannot rename permission row id=%s name='%s' to authority '%s' for %s: another row"
                + " already uses that name.")
            .formatted(conflictingId, conflictingName, permission.authority(), permission.name());
    return new RBACCatalogSynchronizationException(message);
  }

  public static RBACCatalogSynchronizationException roleNotFound(String roleName) {
    return new RBACCatalogSynchronizationException(
        "Role '%s' could not be resolved while synchronizing the RBAC catalog".formatted(roleName));
  }

  public static RBACCatalogSynchronizationException permissionNotFound(
      String roleName, EPermission permission) {
    String message =
        ("Permission %s (authority '%s') required by role '%s' was not found or created during"
                + " catalog synchronization")
            .formatted(permission.name(), permission.authority(), roleName);
    return new RBACCatalogSynchronizationException(message);
  }
}
