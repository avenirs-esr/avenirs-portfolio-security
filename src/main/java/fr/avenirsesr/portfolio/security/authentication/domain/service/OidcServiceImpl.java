package fr.avenirsesr.portfolio.security.authentication.domain.service;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCProfile;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.OidcAuthenticationPort;
import fr.avenirsesr.portfolio.security.principal.domain.exception.PrincipalNotFoundException;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.port.input.PrincipalService;
import java.util.Optional;

public class OidcServiceImpl implements OidcService {

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
  public OIDCAccessToken exchangeAuthorizationCodeForToken(String host, String code) {
    return oidcAuthenticationPort.exchangeAuthorizationCodeForToken(host, code);
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
  public String generateAuthorizationUrl(String host, String redirect) {
    return oidcAuthenticationPort.generateAuthorizationUrl(host, redirect);
  }
}
