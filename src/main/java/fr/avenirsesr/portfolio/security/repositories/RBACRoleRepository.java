package fr.avenirsesr.portfolio.security.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import fr.avenirsesr.portfolio.security.models.RBACRole;

/**
 * Repository for Role.
 */
public interface RBACRoleRepository extends JpaRepository<RBACRole, Long>  {

}
