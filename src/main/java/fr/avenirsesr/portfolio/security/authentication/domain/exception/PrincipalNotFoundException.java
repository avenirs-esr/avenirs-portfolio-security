package fr.avenirsesr.portfolio.security.authentication.domain.exception;

public class PrincipalNotFoundException extends RuntimeException {
  public PrincipalNotFoundException(String message) {
    super(message);
  }

  public PrincipalNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
