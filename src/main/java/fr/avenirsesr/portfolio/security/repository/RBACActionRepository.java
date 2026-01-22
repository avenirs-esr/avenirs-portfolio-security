package fr.avenirsesr.portfolio.security.repository;

import fr.avenirsesr.portfolio.security.model.RBACAction;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 *
 * <h1>RBACActionRepository</h1>
 *
 * <p>Description: Repository for the RBACAction objects.
 *
 * <h2>Version:</h2>
 *
 * 1.0
 *
 * <h2>Author:</h2>
 *
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 *
 * 1 Oct 2024
 */
public interface RBACActionRepository
    extends JpaRepository<RBACAction, UUID>, JpaSpecificationExecutor<RBACAction> {

  /**
   * Find an action by its name.
   *
   * @param name The name of the action to retrieve.
   * @return An Optional of RBACAction.
   */
  Optional<RBACAction> findByName(String name);
}
