package fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACContext;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RBACContextService {

  List<RBACContext> getAllContexts();

  Optional<RBACContext> getContextById(UUID contextId);

  RBACContext createContext(RBACContext context);

  RBACContext updateContext(RBACContext context);

  void deleteContext(UUID contextId);
}
