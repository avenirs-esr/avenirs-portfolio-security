package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.model;

import java.util.Set;

public record SignedContextPayload(
    String sub, long iat, long exp, Set<String> authorities, Set<String> roles) {}
