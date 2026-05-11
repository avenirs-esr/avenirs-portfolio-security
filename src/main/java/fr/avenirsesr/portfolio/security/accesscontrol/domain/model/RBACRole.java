package fr.avenirsesr.portfolio.security.accesscontrol.domain.model;

import java.util.Set;
import java.util.UUID;

public record RBACRole(UUID id, String name, String description, Set<RBACPermission> permissions) {}
