package fr.avenirsesr.portfolio.security.accesscontrol.domain.model;

import java.util.UUID;

public record RBACResourceType(UUID id, String name, String description) {}
