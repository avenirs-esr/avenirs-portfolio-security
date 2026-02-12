package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCProfile;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.model.OIDCProfileResponse;

public final class OIDCProfileMapper {

  private OIDCProfileMapper() {}

  public static OIDCProfile toDomain(OIDCProfileResponse payload) {
    if (payload == null) {
      return null;
    }

    return new OIDCProfile(
        payload.getId(),
        payload.getService(),
        payload.getFirstName(),
        payload.getLastName(),
        payload.getEmail());
  }

  public static OIDCProfileResponse fromDomain(OIDCProfile domain) {
    if (domain == null) {
      return null;
    }

    OIDCProfileResponse payload = new OIDCProfileResponse();
    payload.setId(domain.id());
    payload.setService(domain.service());
    payload.setFirstName(domain.firstName());
    payload.setLastName(domain.lastName());
    payload.setEmail(domain.email());
    return payload;
  }
}
