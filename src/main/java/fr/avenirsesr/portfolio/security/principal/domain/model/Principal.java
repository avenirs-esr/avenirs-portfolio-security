package fr.avenirsesr.portfolio.security.principal.domain.model;

import java.util.Set;
import java.util.UUID;

public record Principal(
    UUID id,
    String login,
    String provider,
    String externalId,
    UUID userId,
    Set<Structure> structures) {}
