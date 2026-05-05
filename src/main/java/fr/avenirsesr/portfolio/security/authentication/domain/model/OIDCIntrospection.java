package fr.avenirsesr.portfolio.security.authentication.domain.model;

import java.util.UUID;

public record OIDCIntrospection(
    String token, boolean active, String uniqueSecurityName, UUID userId) {}
