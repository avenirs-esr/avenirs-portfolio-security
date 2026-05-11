package fr.avenirsesr.portfolio.security.accesscontrol.domain.model;

import java.util.UUID;

public record RBACPermission(UUID id, String name, String description) {}
