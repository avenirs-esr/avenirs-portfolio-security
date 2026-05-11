package fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACResource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RBACResourceService {

  Optional<RBACResource> getResourceById(UUID resourceId);

  List<RBACResource> getAllResources();

  RBACResource createResource(RBACResource resource);

  RBACResource updateResource(RBACResource resource);

  void deleteResource(UUID resourceId);
}
