package fr.avenirsesr.portfolio.security.accesscontrol.domain.model;

import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import java.util.UUID;

public record RBACAssignment(
    UUID id, Principal principal, RBACRole role, RBACScope scope, RBACContext context) {}
