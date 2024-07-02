package fr.avenirsesr.portfolio.security.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import fr.avenirsesr.portfolio.security.models.RBACAssignment;
import fr.avenirsesr.portfolio.security.models.Principal;

/**
 * Repository for Role.JpaRepository<Customer>, JpaSpecificationExecutor
 */
public interface AssignmentRepository extends CrudRepository<RBACAssignment, Long>, JpaSpecificationExecutor<RBACAssignment>  {
	List<RBACAssignment> findByPrincipal(Principal principal);

}
