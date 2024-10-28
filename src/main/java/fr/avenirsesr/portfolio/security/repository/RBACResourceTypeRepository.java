package fr.avenirsesr.portfolio.security.repository;

import fr.avenirsesr.portfolio.security.model.RBACResourceType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <h1>RBACResourceRepository</h1>
 * <p>
 * <b>Description:</b> Repository for RBACResourceType models.
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 07/10/2024
 */

public interface RBACResourceTypeRepository extends JpaRepository<RBACResourceType, Long>  {

}
