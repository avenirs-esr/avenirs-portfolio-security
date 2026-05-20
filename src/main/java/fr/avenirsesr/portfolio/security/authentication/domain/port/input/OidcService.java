package fr.avenirsesr.portfolio.security.authentication.domain.port.input;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCProfile;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import java.util.Optional;

public interface OidcService {
  Optional<OIDCAccessToken> getAccessToken(String login, String password);

  OIDCAccessToken exchangeAuthorizationCodeForToken(String host, String code, String codeVerifier);

  String generateServiceURL(String host);

  OIDCIntrospection introspectAccessToken(String token);

  OIDCProfile profile(String token);

  String generateAuthorizationUrl(String host, String redirect, String codeChallenge);

  OIDCSession refreshSessionIfNeeded(OIDCSession oidcSession);
}
