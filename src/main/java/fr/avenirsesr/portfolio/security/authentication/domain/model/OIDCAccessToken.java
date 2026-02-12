package fr.avenirsesr.portfolio.security.authentication.domain.model;

import java.util.Map;

public record OIDCAccessToken(
    String accessToken,
    String tokenType,
    int expiresIn,
    String scope,
    String rawIdToken,
    Map<String, Object> claims,
    boolean jwt) {}
