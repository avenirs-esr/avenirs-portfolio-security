package fr.avenirsesr.portfolio.security.authentication.domain.model;

public record SignedAuthContext(String payload, String signature) {}
