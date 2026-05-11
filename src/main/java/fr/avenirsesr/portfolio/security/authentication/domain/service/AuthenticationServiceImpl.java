package fr.avenirsesr.portfolio.security.authentication.domain.service;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCProfile;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.AuthenticationPort;
import fr.avenirsesr.portfolio.security.principal.domain.exception.PrincipalNotFoundException;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.port.input.PrincipalService;
import java.util.Optional;

public class AuthenticationServiceImpl implements AuthenticationService {

  private final AuthenticationPort authenticationPort;
  private final PrincipalService principalService;

  public AuthenticationServiceImpl(
      AuthenticationPort authenticationPort, PrincipalService principalService) {
    this.authenticationPort = authenticationPort;
    this.principalService = principalService;
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

    OIDCIntrospection introspection = authenticationPort.introspectAccessToken(token);

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
    return authenticationPort.profile(token);
  }
}
