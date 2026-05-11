package fr.avenirsesr.portfolio.security.accesscontrol.domain.exception;

public class AccessControlInvalidDateException extends RuntimeException {

  public AccessControlInvalidDateException(String dateFormat, String value, Throwable cause) {
    super("Invalid date format (%s): %s".formatted(dateFormat, value), cause);
  }
}
