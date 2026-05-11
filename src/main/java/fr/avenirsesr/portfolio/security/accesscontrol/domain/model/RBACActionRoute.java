package fr.avenirsesr.portfolio.security.accesscontrol.domain.model;

import java.util.UUID;

public record RBACActionRoute(UUID id, String uri, String method, RBACAction action) {}
