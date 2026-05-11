package fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACActionRoute;
import java.util.Optional;

public interface RBACActionRouteService {

  Optional<RBACActionRoute> findByUriAndMethod(String uri, String method);
}
