package fr.avenirsesr.portfolio.security.authentication.domain.service;

import fr.avenirsesr.portfolio.security.authentication.domain.exception.UnauthenticatedSessionException;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCProfile;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.OidcAuthenticationPort;
import fr.avenirsesr.portfolio.security.principal.domain.exception.PrincipalNotFoundException;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.port.input.PrincipalService;
import java.time.Instant;
import java.util.Optional;

public class OidcServiceImpl implements OidcService {

  private static final long REFRESH_SKEW_SECONDS = 30;

  private final OidcAuthenticationPort oidcAuthenticationPort;
  private final PrincipalService principalService;

  public OidcServiceImpl(
      OidcAuthenticationPort oidcAuthenticationPort, PrincipalService principalService) {
    this.oidcAuthenticationPort = oidcAuthenticationPort;
    this.principalService = principalService;
  }

  @Override
  public Optional<OIDCAccessToken> getAccessToken(String login, String password) {
    return oidcAuthenticationPort.getAccessToken(login, password);
  }

  @Override
  public OIDCAccessToken exchangeAuthorizationCodeForToken(
      String host, String code, String codeVerifier) {
    return oidcAuthenticationPort.exchangeAuthorizationCodeForToken(host, code, codeVerifier);
  }

  @Override
  public String generateServiceURL(String host) {
    return oidcAuthenticationPort.generateServiceURL(host);
  }

  @Override
  public OIDCIntrospection introspectAccessToken(String token) {

    OIDCIntrospection introspection = oidcAuthenticationPort.introspectAccessToken(token);

    if (!introspection.active()) {
      return introspection;
    }

    Principal principal =
        principalService
            .getPrincipalByProviderAndExternalId("OIDC", introspection.uniqueSecurityName())
            .orElseThrow(
                () ->
                    new PrincipalNotFoundException(
                        "No principal found for external id: "
                            + introspection.uniqueSecurityName()));

    return new OIDCIntrospection(
        introspection.token(), true, introspection.uniqueSecurityName(), principal.userId());
  }

  @Override
  public OIDCProfile profile(String token) {
    return oidcAuthenticationPort.profile(token);
  }

  @Override
  public String generateAuthorizationUrl(String host, String redirect, String codeChallenge) {
    return oidcAuthenticationPort.generateAuthorizationUrl(host, redirect, codeChallenge);
  }

  @Override
  public OIDCSession refreshSessionIfNeeded(OIDCSession session) {
    if (session.accessTokenExpiresAt().isAfter(Instant.now().plusSeconds(REFRESH_SKEW_SECONDS))) {
      return session;
    }

    if (session.refreshToken() == null || session.refreshToken().isBlank()) {
      throw new UnauthenticatedSessionException();
    }

    OIDCAccessToken refreshedToken =
        oidcAuthenticationPort.refreshAccessToken(session.refreshToken());

    return new OIDCSession(
        refreshedToken.accessToken(),
        refreshedToken.refreshToken() != null
            ? refreshedToken.refreshToken()
            : session.refreshToken(),
        refreshedToken.rawIdToken(),
        Instant.now().plusSeconds(refreshedToken.expiresIn()));
  }
}
