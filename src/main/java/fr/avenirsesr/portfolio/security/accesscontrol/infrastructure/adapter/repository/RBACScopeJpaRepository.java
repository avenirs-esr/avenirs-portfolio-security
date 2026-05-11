package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACScopeEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 *
 * <h1>RBACScopeRepository</h1>
 *
 * <p><b>Description:</b> Repository for RBACScope entities.
 *
 * <h2>Version:</h2>
 *
 * 1.0.0
 *
 * <h2>Author:</h2>
 *
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 *
 * 16/10/2024
 */
public interface RBACScopeJpaRepository
    extends JpaRepository<RBACScopeEntity, UUID>, JpaSpecificationExecutor<RBACScopeEntity> {
  /**
   * Find a scope by its name.
   *
   * @param name The name of the scope to retrieve.
   * @return An Optional of RBACScope.
   */
  Optional<RBACScopeEntity> findByName(String name);
}
