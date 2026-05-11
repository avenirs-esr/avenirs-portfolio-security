package fr.avenirsesr.portfolio.security.accesscontrol.domain.model;

import java.util.List;
import java.util.UUID;

public record RBACScope(UUID id, List<RBACResource> resources) {}
