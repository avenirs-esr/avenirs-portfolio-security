package fr.avenirsesr.portfolio.security.repositories;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import fr.avenirsesr.portfolio.security.models.RBACAction;

/**
 * Repository for RBACAction.
 */
public interface RBACActionRepository extends CrudRepository<RBACAction, Long>  {
	
	Optional<RBACAction> findByName(String name);

}
