package fr.avenirsesr.portfolio.security.authentication.domain.service;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCProfile;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.AuthenticationPort;
import java.util.Optional;

public class AuthenticationServiceImpl implements AuthenticationService {

  private final AuthenticationPort authenticationPort;

  public AuthenticationServiceImpl(AuthenticationPort authenticationPort) {
    this.authenticationPort = authenticationPort;
  }

  @Override
  public Optional<OIDCAccessToken> getAccessToken(String login, String password) {
    return authenticationPort.getAccessToken(login, password);
  }

  @Override
  public OIDCAccessToken exchangeAuthorizationCodeForToken(String host, String code) {
    return authenticationPort.exchangeAuthorizationCodeForToken(host, code);
  }

  @Override
  public String generateServiceURL(String host) {
    return authenticationPort.generateServiceURL(host);
  }

  @Override
  public OIDCIntrospection introspectAccessToken(String token) {
    return authenticationPort.introspectAccessToken(token);
  }

  @Override
  public OIDCProfile profile(String token) {
    return authenticationPort.profile(token);
  }
}
