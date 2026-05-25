package fr.avenirsesr.portfolio.security.authentication.application.adapter.mapper;

import static fr.avenirsesr.portfolio.security.shared.infrastructure.configuration.UserServiceConfig.USER_ID_MOCK;

import fr.avenirsesr.portfolio.security.authentication.application.adapter.dto.IntrospectResponseDTO;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import java.util.UUID;

public final class IntrospectResponseMapper {
  private IntrospectResponseMapper() {}

  // TODO: Mock while waiting #1683
  public static IntrospectResponseDTO toDTO(OIDCIntrospection introspection) {
    return new IntrospectResponseDTO(
        introspection.active(), introspection.uniqueSecurityName(), UUID.fromString(USER_ID_MOCK));
  }
}
