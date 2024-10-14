package fr.avenirsesr.portfolio.security.repositories;

import fr.avenirsesr.portfolio.security.models.RBACResource;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <h1>RBACResourceRepository</h1>
 * <p>
 * <b>Description:</b> Repository for RBACResource models.
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

public interface RBACResourceRepository extends JpaRepository<RBACResource, Long>  {

}
