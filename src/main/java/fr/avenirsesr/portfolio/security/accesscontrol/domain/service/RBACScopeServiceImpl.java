package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlNotFoundException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACScope;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.RBACScopeService;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACScopeRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RBACScopeServiceImpl implements RBACScopeService {

  private final RBACScopeRepository scopeRepository;

  @Override
  public Optional<RBACScope> getScopeById(UUID scopeId) {
    log.trace("getScopeById, scopeId: {}", scopeId);
    return scopeRepository.findById(scopeId);
  }

  @Override
  public Optional<RBACScope> getScopeByName(String scopeName) {
    log.trace("getScopeByName, scopeName: {}", scopeName);
    return scopeRepository.findByName(scopeName);
  }

  @Override
  public List<RBACScope> getAllScopes() {
    log.trace("getAllScopes");
    return scopeRepository.findAll();
  }

  @Override
  public RBACScope createScope(RBACScope scope) {
    log.trace("createScope, scope: {}", scope);
    return scopeRepository.save(scope);
  }

  @Override
  public RBACScope updateScope(RBACScope scope) {
    log.trace("updateScope, scope: {}", scope);

    scopeRepository
        .findById(scope.id())
        .orElseThrow(() -> AccessControlNotFoundException.scope(scope.id()));

    return scopeRepository.save(scope);
  }

  @Override
  public void deleteScope(UUID scopeId) {
    log.trace("deleteScope, scopeId: {}", scopeId);
    scopeRepository.deleteById(scopeId);
  }
}
