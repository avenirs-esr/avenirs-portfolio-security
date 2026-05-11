package fr.avenirsesr.portfolio.security.accesscontrol.domain.model;

import java.util.List;
import java.util.UUID;

public record RBACAction(
    UUID id, String name, String description, List<RBACPermission> permissions) {}
