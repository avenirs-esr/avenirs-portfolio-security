package fr.avenirsesr.portfolio.security.principal.domain.exception;

public class PrincipalNotFoundException extends RuntimeException {
  public PrincipalNotFoundException(String message) {
    super(message);
  }

  public PrincipalNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
