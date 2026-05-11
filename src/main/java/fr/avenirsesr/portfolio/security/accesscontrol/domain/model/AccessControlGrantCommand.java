package fr.avenirsesr.portfolio.security.accesscontrol.domain.model;

import java.util.List;
import java.util.UUID;

public record AccessControlGrantCommand(
    String login,
    UUID roleId,
    List<UUID> resourceIds,
    String validityStart,
    String validityEnd,
    List<UUID> structureIds) {}
