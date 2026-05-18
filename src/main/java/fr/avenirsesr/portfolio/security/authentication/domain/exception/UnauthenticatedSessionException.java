package fr.avenirsesr.portfolio.security.authentication.domain.exception;

public class UnauthenticatedSessionException extends RuntimeException {
  public UnauthenticatedSessionException() {
    super("Unauthenticated session");
  }
}
