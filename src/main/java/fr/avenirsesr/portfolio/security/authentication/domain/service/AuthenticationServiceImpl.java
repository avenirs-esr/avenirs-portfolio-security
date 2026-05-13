package fr.avenirsesr.portfolio.security.authentication.domain.service;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.OidcAuthenticationPort;
import java.time.Instant;

public class AuthenticationServiceImpl implements AuthenticationService {

  private final OidcAuthenticationPort oidcAuthenticationPort;

  public AuthenticationServiceImpl(OidcAuthenticationPort oidcAuthenticationPort) {
    this.oidcAuthenticationPort = oidcAuthenticationPort;
  }

  @Override
  public String generateAuthorizationUrl(String host, String redirect) {
    return oidcAuthenticationPort.generateAuthorizationUrl(host, redirect);
  }

  @Override
  public OIDCSession createSessionFromAuthorizationCode(String host, String code) {
    OIDCAccessToken accessToken =
        oidcAuthenticationPort.exchangeAuthorizationCodeForToken(host, code);

    Instant accessTokenExpiresAt = Instant.now().plusSeconds(accessToken.expiresIn());

    return new OIDCSession(
        accessToken.accessToken(),
        accessToken.refreshToken(),
        accessToken.rawIdToken(),
        accessTokenExpiresAt);
  }
}
