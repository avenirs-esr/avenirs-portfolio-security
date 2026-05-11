package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.seeder.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record PrincipalCreationData(
    String login,
    String provider,
    @JsonProperty("external_id") String externalId,
    @JsonProperty("user_id") UUID userId) {}
