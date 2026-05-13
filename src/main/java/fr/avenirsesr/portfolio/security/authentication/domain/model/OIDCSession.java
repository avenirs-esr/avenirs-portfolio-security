package fr.avenirsesr.portfolio.security.authentication.domain.model;

import java.time.Instant;

public record OIDCSession(
    String accessToken, String refreshToken, String idToken, Instant accessTokenExpiresAt) {}
