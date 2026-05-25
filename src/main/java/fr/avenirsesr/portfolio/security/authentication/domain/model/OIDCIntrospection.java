package fr.avenirsesr.portfolio.security.authentication.domain.model;

public record OIDCIntrospection(String token, boolean active, String uniqueSecurityName) {}
