package fr.avenirsesr.portfolio.security.accesscontrol.domain.model;

import fr.avenirsesr.portfolio.common.security.accesscontrol.domain.model.enums.ERole;
import java.util.List;
import java.util.UUID;

public record AccessControlGrantCommand(
    String login,
    ERole role,
    List<UUID> resourceIds,
    String validityStart,
    String validityEnd,
    List<UUID> structureIds) {}
