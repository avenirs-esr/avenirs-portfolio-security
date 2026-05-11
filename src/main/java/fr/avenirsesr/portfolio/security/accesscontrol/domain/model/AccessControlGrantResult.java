package fr.avenirsesr.portfolio.security.accesscontrol.domain.model;

import java.util.UUID;

public record AccessControlGrantResult(
    String login, boolean granted, UUID assignmentId, String error) {}
