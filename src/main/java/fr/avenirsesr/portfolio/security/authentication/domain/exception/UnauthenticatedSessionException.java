package fr.avenirsesr.portfolio.security.authentication.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class UnauthenticatedSessionException extends RuntimeException {
  public UnauthenticatedSessionException() {
    super(EErrorCode.UNAUTHENTICATED_SESSION.getMessage());
  }
}
