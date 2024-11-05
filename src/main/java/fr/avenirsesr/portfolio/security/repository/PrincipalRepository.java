package fr.avenirsesr.portfolio.security.repository;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.avenirsesr.portfolio.security.model.Principal;

/**
 * Repository for Principal.
 */
public interface PrincipalRepository extends JpaRepository<Principal, UUID>  {
	Optional<Principal> findByLogin(String login);
}
