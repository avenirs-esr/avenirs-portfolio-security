package fr.avenirsesr.portfolio.security.authentication.application.adapter.mapper;

import fr.avenirsesr.portfolio.security.authentication.application.adapter.dto.AuthContextDTO;
import fr.avenirsesr.portfolio.security.authentication.domain.model.AuthContext;

public final class AuthContextMapper {
  private AuthContextMapper() {}

  public static AuthContextDTO toDTO(AuthContext authContext) {
    return new AuthContextDTO(
        authContext.authenticated(), authContext.userId(), authContext.login());
  }
}
