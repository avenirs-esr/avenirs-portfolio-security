package fr.avenirsesr.portfolio.security.authentication.domain.port.output;

import java.util.Set;

public interface AuthorizationsPort {
  Set<String> resolveAuthorities(String login);
}
