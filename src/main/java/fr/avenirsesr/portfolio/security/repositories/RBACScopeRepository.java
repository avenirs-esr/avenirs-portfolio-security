package fr.avenirsesr.portfolio.security.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import fr.avenirsesr.portfolio.security.models.RBACScope;

/**
 * Repository for RBACScope.
 */
public interface RBACScopeRepository extends JpaRepository<RBACScope, Long>, JpaSpecificationExecutor<RBACScope> {

}
