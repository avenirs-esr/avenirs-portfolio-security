package fr.avenirsesr.portfolio.security.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import fr.avenirsesr.portfolio.security.model.RBACScope;

import java.util.Optional;
import java.util.UUID;

/**
 * <h1>RBACScopeRepository</h1>
 * <p>
 * <b>Description:</b> Repository for RBACScope entities.
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 16/10/2024
 */
public interface RBACScopeRepository extends JpaRepository<RBACScope, UUID>, JpaSpecificationExecutor<RBACScope> {
    /**
     * Find a scope by its name.
     * @param name The name of the scope to retrieve.
     * @return An Optional of RBACScope.
     */
    Optional<RBACScope> findByName(String name);
}
