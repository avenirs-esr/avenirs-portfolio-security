package fr.avenirsesr.portfolio.security.accesscontrol.domain.model;

import java.util.UUID;

public record AccessControlRevokeResult(
    String login, boolean revoked, UUID assignmentId, String error) {}
