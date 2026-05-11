package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model.PrincipalEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository for Principal. */
public interface PrincipalJpaRepository extends JpaRepository<PrincipalEntity, UUID> {
  Optional<PrincipalEntity> findByLogin(String login);

  Optional<PrincipalEntity> findByProviderAndExternalId(String provider, String externalId);
}
