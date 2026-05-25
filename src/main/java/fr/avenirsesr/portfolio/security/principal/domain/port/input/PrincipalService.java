package fr.avenirsesr.portfolio.security.principal.domain.port.input;

import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import java.util.List;
import java.util.Optional;

public interface PrincipalService {

  List<Principal> getAllPrincipals();

  Optional<Principal> getPrincipalByLogin(String login);

  Optional<Principal> getPrincipalByProviderAndExternalId(String provider, String externalId);

  Optional<Principal> getPrincipalByEppn(String eppn);
}
