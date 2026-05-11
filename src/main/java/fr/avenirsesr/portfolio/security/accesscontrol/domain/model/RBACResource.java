package fr.avenirsesr.portfolio.security.accesscontrol.domain.model;

import java.util.UUID;

public record RBACResource(UUID id, String selector, RBACResourceType resourceType) {}
