package fr.avenirsesr.portfolio.security.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import fr.avenirsesr.portfolio.security.models.RBACRole;

import java.util.Optional;

/**
 * <h1>RBACRoleRepository</h1>
 * <p>
 * <b>Description:</b> Repository for RBAC Roles.
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 15/10/2024
 */

public interface RBACRoleRepository extends JpaRepository<RBACRole, Long>  {
    /**
     * Find a role by its name.
     * @param name The name of the role to retrieve.
     * @return An Optional of RBACRole.
     */
    Optional<RBACRole> findByName(String name);

}
