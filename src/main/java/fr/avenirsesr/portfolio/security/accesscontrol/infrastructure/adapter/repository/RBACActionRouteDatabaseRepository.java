package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACActionRoute;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACActionRouteRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper.RBACActionRouteMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RBACActionRouteDatabaseRepository implements RBACActionRouteRepository {

  private final RBACActionRouteJpaRepository actionRouteJpaRepository;
  private final RBACActionRouteMapper actionRouteMapper;

  @Override
  @Transactional
  public Optional<RBACActionRoute> findByUriAndMethod(String uri, String method) {
    return actionRouteJpaRepository
        .findByUriAndMethod(uri, method)
        .map(actionRouteMapper::toDomain);
  }
}
