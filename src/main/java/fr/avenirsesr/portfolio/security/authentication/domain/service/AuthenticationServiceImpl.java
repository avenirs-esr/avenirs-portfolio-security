package fr.avenirsesr.portfolio.security.authentication.domain.service;

import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.OidcAuthenticationPort;

public class AuthenticationServiceImpl implements AuthenticationService {

  private final OidcAuthenticationPort oidcAuthenticationPort;

  public AuthenticationServiceImpl(OidcAuthenticationPort oidcAuthenticationPort) {
    this.oidcAuthenticationPort = oidcAuthenticationPort;
  }

  @Override
  public String generateAuthorizationUrl(String host, String redirect) {
    return oidcAuthenticationPort.generateAuthorizationUrl(host, redirect);
  }
}
