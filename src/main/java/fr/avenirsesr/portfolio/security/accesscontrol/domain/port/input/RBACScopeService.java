package fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACScope;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RBACScopeService {

  Optional<RBACScope> getScopeById(UUID scopeId);

  Optional<RBACScope> getScopeByName(String scopeName);

  List<RBACScope> getAllScopes();

  RBACScope createScope(RBACScope scope);

  RBACScope updateScope(RBACScope scope);

  void deleteScope(UUID scopeId);
}
