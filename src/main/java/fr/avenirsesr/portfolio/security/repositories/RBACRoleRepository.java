package fr.avenirsesr.portfolio.security.repositories;
import org.springframework.data.repository.CrudRepository;

import fr.avenirsesr.portfolio.security.models.RBACRole;

/**
 * Repository for Role.
 */
public interface RBACRoleRepository extends CrudRepository<RBACRole, Long>  {

}
