package fr.avenirsesr.portfolio.security.authentication.domain.model;

public record PkceChallenge(String verifier, String challenge) {}
