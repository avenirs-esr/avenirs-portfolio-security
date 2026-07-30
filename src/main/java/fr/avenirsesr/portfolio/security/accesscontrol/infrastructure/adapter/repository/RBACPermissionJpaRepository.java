package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACPermissionEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 *
 * <h1>RBACPermissionJpaRepository</h1>
 *
 * <p><b>Description:</b> Repository for RBAC Permissions.
 */
public interface RBACPermissionJpaRepository extends JpaRepository<RBACPermissionEntity, UUID> {
  /**
   * Find a permission by its name.
   *
   * @param name The name of the permission to retrieve.
   * @return An Optional of RBACPermissionEntity.
   */
  Optional<RBACPermissionEntity> findByName(String name);
}
