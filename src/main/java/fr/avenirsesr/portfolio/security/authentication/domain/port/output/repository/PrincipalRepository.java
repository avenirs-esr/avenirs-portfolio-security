package fr.avenirsesr.portfolio.security.authentication.domain.port.output.repository;

import fr.avenirsesr.portfolio.security.model.Principal;
import java.util.Optional;

public interface PrincipalRepository {
  Optional<Principal> findByProviderAndExternalId(String provider, String externalId);
}
