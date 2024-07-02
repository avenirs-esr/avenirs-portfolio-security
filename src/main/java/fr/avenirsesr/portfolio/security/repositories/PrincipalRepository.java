package fr.avenirsesr.portfolio.security.repositories;
import org.springframework.data.repository.CrudRepository;

import fr.avenirsesr.portfolio.security.models.Principal;

/**
 * Repository for Principal.
 */
public interface PrincipalRepository extends CrudRepository<Principal, Long>  {

}
