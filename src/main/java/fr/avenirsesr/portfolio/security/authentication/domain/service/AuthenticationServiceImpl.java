package fr.avenirsesr.portfolio.security.authentication.domain.service;

import fr.avenirsesr.portfolio.security.authentication.domain.exception.UnauthenticatedSessionException;
import fr.avenirsesr.portfolio.security.authentication.domain.model.AuthContext;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.port.input.PrincipalService;
import java.time.Instant;

public class AuthenticationServiceImpl implements AuthenticationService {

  private final OidcService oidcService;
  private final PrincipalService principalService;

  public AuthenticationServiceImpl(OidcService oidcService, PrincipalService principalService) {
    this.oidcService = oidcService;
    this.principalService = principalService;
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
  public AuthContext getAuthenticatedContext(OIDCSession oidcSession) {
    OIDCIntrospection introspection = oidcService.introspectAccessToken(oidcSession.accessToken());

    if (introspection == null || !introspection.active()) {
      throw new UnauthenticatedSessionException();
    }

    Principal principal =
        principalService
            .getPrincipalByProviderAndExternalId("OIDC", introspection.uniqueSecurityName())
            .orElseThrow(UnauthenticatedSessionException::new);

    return new AuthContext(true, principal.userId(), introspection.uniqueSecurityName());
  }
}
