package fr.avenirsesr.portfolio.security.authentication.domain.model;

import java.util.UUID;

public record AuthContext(boolean authenticated, UUID userId, String login) {}
