package fr.avenirsesr.portfolio.security.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.avenirsesr.portfolio.security.models.Principal;

/**
 * Repository for Principal.
 */
public interface PrincipalRepository extends JpaRepository<Principal, Long>  {
	Optional<Principal> findByLogin(String login);
}
