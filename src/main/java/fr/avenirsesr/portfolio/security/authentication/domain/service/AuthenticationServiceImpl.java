package fr.avenirsesr.portfolio.security.authentication.domain.service;

import fr.avenirsesr.portfolio.security.authentication.domain.exception.UnauthenticatedSessionException;
import fr.avenirsesr.portfolio.security.authentication.domain.model.*;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.AuthContextSigningPort;
import java.time.Instant;

public class AuthenticationServiceImpl implements AuthenticationService {

  private final OidcService oidcService;
  private final AuthContextSigningPort authContextSigningPort;

  public AuthenticationServiceImpl(
      OidcService oidcService, AuthContextSigningPort authContextSigningPort) {
    this.oidcService = oidcService;
    this.authContextSigningPort = authContextSigningPort;
  }

  @Override
  public String generateAuthorizationUrl(String host, String redirect, String codeChallenge) {
    return oidcService.generateAuthorizationUrl(host, redirect, codeChallenge);
  }

  @Override
  public OIDCSession createSessionFromAuthorizationCode(
      String host, String code, String codeVerifier) {
    OIDCAccessToken accessToken =
        oidcService.exchangeAuthorizationCodeForToken(host, code, codeVerifier);

    Instant accessTokenExpiresAt = Instant.now().plusSeconds(accessToken.expiresIn());

    return new OIDCSession(
        accessToken.accessToken(),
        accessToken.refreshToken(),
        accessToken.rawIdToken(),
        accessTokenExpiresAt);
  }

  @Override
  public SignedAuthContext getSignedAuthenticatedContext(OIDCSession oidcSession) {
    AuthContext authContext = getAuthenticatedContext(oidcSession);
    return authContextSigningPort.sign(authContext);
  }

  private AuthContext getAuthenticatedContext(OIDCSession oidcSession) {
    OIDCIntrospection introspection = oidcService.introspectAccessToken(oidcSession.accessToken());

    if (introspection == null || !introspection.active()) {
      throw new UnauthenticatedSessionException();
    }

    return new AuthContext(true, introspection.uniqueSecurityName());
  }

  @Override
  public OIDCSession refreshSessionIfNeeded(OIDCSession oidcSession) {
    return oidcService.refreshSessionIfNeeded(oidcSession);
  }
}
