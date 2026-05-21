package fr.avenirsesr.portfolio.security.authentication.application.adapter.mapper;

import fr.avenirsesr.portfolio.security.authentication.application.adapter.dto.SignedAuthContextDTO;
import fr.avenirsesr.portfolio.security.authentication.domain.model.SignedAuthContext;

public final class SignedAuthContextMapper {
  private SignedAuthContextMapper() {}

  public static SignedAuthContextDTO toDTO(SignedAuthContext signedAuthContext) {
    return new SignedAuthContextDTO(
        signedAuthContext.payload(), signedAuthContext.signature(), signedAuthContext.kid());
  }
}
