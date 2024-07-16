package fr.avenirsesr.portfolio.security.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import fr.avenirsesr.portfolio.security.models.RBACAction;
import fr.avenirsesr.portfolio.security.models.RBACScope;

/**
 * Repository for RBACAction.
 */
public interface RBACActionRepository extends JpaRepository<RBACAction, Long>, JpaSpecificationExecutor<RBACAction>   {
	
	Optional<RBACAction> findByName(String name);

}
