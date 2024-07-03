package fr.avenirsesr.portfolio.security.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.avenirsesr.portfolio.security.models.RBACAction;

/**
 * Repository for RBACAction.
 */
public interface RBACActionRepository extends JpaRepository<RBACAction, Long>  {
	
	Optional<RBACAction> findByName(String name);

}
