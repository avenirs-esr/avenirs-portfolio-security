package fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACActionRoute;
import java.util.Optional;

public interface RBACActionRouteRepository {

  Optional<RBACActionRoute> findByUriAndMethod(String uri, String method);
}
