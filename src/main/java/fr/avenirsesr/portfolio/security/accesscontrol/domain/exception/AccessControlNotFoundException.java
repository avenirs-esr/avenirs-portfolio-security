package fr.avenirsesr.portfolio.security.accesscontrol.domain.exception;

import java.util.Collection;
import java.util.UUID;

public class AccessControlNotFoundException extends RuntimeException {

  private AccessControlNotFoundException(String message) {
    super(message);
  }

  public static AccessControlNotFoundException role(UUID roleId) {
    return new AccessControlNotFoundException("Role not found, ID: %s".formatted(roleId));
  }

  public static AccessControlNotFoundException principal(String login) {
    return new AccessControlNotFoundException("Principal not found, login: %s".formatted(login));
  }

  public static AccessControlNotFoundException resources(Collection<UUID> ids) {
    return new AccessControlNotFoundException("Missing resources, IDs: %s".formatted(ids));
  }

  public static AccessControlNotFoundException structures(Collection<UUID> ids) {
    return new AccessControlNotFoundException("Missing structures, IDs: %s".formatted(ids));
  }

  public static AccessControlNotFoundException assignment(UUID assignmentId) {
    return new AccessControlNotFoundException(
        "Assignment not found, ID: %s".formatted(assignmentId));
  }

  public static AccessControlNotFoundException resource(UUID resourceId) {
    return new AccessControlNotFoundException("Resource not found, ID: %s".formatted(resourceId));
  }

  public static AccessControlNotFoundException context(UUID contextId) {
    return new AccessControlNotFoundException("Context not found, ID: %s".formatted(contextId));
  }

  public static AccessControlNotFoundException scope(UUID scopeId) {
    return new AccessControlNotFoundException("Scope not found, ID: %s".formatted(scopeId));
  }
}
