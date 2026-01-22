package fr.avenirsesr.portfolio.security.authentication.domain.model;

public record OIDCProfile(
    String id, String service, String firstName, String lastName, String email) {}
