package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;

public record ExternalUserSeedData(
    String eppn,
    String firstName,
    String lastName,
    String email,
    EUserCategory category,
    String externalId,
    String source,
    String status) {}
