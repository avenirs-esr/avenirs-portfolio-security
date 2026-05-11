package fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.AccessControlGrantCommand;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.AccessControlGrantResult;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.AccessControlRevokeCommand;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.AccessControlRevokeResult;
import java.util.UUID;

public interface AccessControlService {

  AccessControlGrantResult grantAccess(AccessControlGrantCommand command);

  AccessControlRevokeResult revokeAccess(AccessControlRevokeCommand command);

  boolean isAuthorized(String login, UUID actionId, UUID resourceId);
}
