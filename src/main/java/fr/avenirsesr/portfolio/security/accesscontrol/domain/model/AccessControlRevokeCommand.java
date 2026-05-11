package fr.avenirsesr.portfolio.security.accesscontrol.domain.model;

import java.util.UUID;

public record AccessControlRevokeCommand(String login, UUID assignmentId) {}
