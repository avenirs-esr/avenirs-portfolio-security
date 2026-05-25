package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.model.OIDCIntrospectResponse;

public final class OIDCIntrospectionMapper {

  private OIDCIntrospectionMapper() {}

  public static OIDCIntrospection toDomain(OIDCIntrospectResponse payload) {
    if (payload == null) {
      return null;
    }

    return new OIDCIntrospection(
        payload.getToken(), payload.isActive(), payload.getUniqueSecurityName());
  }

  public static OIDCIntrospectResponse fromDomain(OIDCIntrospection domain) {
    if (domain == null) {
      return null;
    }

    OIDCIntrospectResponse payload = new OIDCIntrospectResponse();
    payload.setToken(domain.token());
    payload.setActive(domain.active());
    payload.setUniqueSecurityName(domain.uniqueSecurityName());
    return payload;
  }
}
