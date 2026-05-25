package fr.avenirsesr.portfolio.security.principal.domain.port.output.repository;

import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import java.util.List;
import java.util.Optional;

public interface PrincipalRepository {

  List<Principal> findAll();

  Optional<Principal> findByLogin(String login);

  Optional<Principal> findByProviderAndExternalId(String provider, String externalId);

  Optional<Principal> findByEppn(String eppn);
}
