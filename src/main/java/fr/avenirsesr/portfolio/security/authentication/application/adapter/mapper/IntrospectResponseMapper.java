package fr.avenirsesr.portfolio.security.authentication.application.adapter.mapper;

import fr.avenirsesr.portfolio.security.authentication.application.adapter.dto.IntrospectResponseDTO;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;

public final class IntrospectResponseMapper {
  private IntrospectResponseMapper() {}

  public static IntrospectResponseDTO toDTO(OIDCIntrospection introspection) {
    return new IntrospectResponseDTO(
        introspection.active(), introspection.uniqueSecurityName(), introspection.userId());
  }
}
