package fr.avenirsesr.portfolio.security.repositories;
import org.springframework.data.repository.CrudRepository;

import fr.avenirsesr.portfolio.security.models.RBACAction;
import fr.avenirsesr.portfolio.security.models.RBACRole;

/**
 * Repository for RBACAction.
 */
public interface RBACActionRepository extends CrudRepository<RBACAction, Long>  {

}
