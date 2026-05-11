package fr.avenirsesr.portfolio.security.principal.domain.exception;

public class StructureNotFoundException extends RuntimeException {
  public StructureNotFoundException(String message) {
    super(message);
  }

  public StructureNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
