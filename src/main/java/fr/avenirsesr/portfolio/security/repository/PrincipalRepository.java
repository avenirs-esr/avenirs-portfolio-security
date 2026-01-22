package fr.avenirsesr.portfolio.security.repository;

import fr.avenirsesr.portfolio.security.model.Principal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository for Principal. */
public interface PrincipalRepository extends JpaRepository<Principal, UUID> {
  Optional<Principal> findByLogin(String login);
}
