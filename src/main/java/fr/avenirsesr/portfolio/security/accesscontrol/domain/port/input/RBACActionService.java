package fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RBACActionService {

  Optional<RBACAction> getActionById(UUID actionId);

  Optional<RBACAction> getActionByName(String name);

  List<RBACAction> getAllActions();
}
