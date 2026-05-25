package fr.avenirsesr.portfolio.security.principal.domain.service;

import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.port.input.PrincipalService;
import fr.avenirsesr.portfolio.security.principal.domain.port.output.repository.PrincipalRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PrincipalServiceImpl implements PrincipalService {

  private final PrincipalRepository principalRepository;

  @Override
  public List<Principal> getAllPrincipals() {
    log.trace("getAllPrincipals");
    return principalRepository.findAll();
  }

  @Override
  public Optional<Principal> getPrincipalByLogin(String login) {
    log.trace("getPrincipalByLogin, login: {}", login);
    return principalRepository.findByLogin(login);
  }

  @Override
  public Optional<Principal> getPrincipalByEppn(String eppn) {
    log.trace("getPrincipalByEppn, eppn: {}", eppn);
    return principalRepository.findByEppn(eppn);
  }

  @Override
  public Optional<Principal> getPrincipalByProviderAndExternalId(
      String provider, String externalId) {
    log.trace(
        "getPrincipalByProviderAndExternalId, provider: {}, externalId: {}", provider, externalId);
    return principalRepository.findByProviderAndExternalId(provider, externalId);
  }
}
