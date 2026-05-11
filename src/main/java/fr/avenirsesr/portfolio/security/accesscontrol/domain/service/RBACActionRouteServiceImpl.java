package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACActionRoute;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.RBACActionRouteService;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACActionRouteRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RBACActionRouteServiceImpl implements RBACActionRouteService {

  private final RBACActionRouteRepository actionRouteRepository;

  @Override
  public Optional<RBACActionRoute> findByUriAndMethod(String uri, String method) {
    log.trace("findByUriAndMethod, uri: {}, method: {}", uri, method);
    return actionRouteRepository.findByUriAndMethod(uri, method);
  }
}
