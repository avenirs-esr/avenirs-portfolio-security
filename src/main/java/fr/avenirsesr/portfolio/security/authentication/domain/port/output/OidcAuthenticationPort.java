package fr.avenirsesr.portfolio.security.authentication.domain.port.output;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCProfile;
import java.util.Optional;

public interface OidcAuthenticationPort {
  String generateAuthorizeURL(String host, String code);

  String generateServiceURL(String host);

  String generateProfileURL(String token);

  String generateIntrospectURL(String token);

  OIDCAccessToken exchangeAuthorizationCodeForToken(String host, String code);

  OIDCProfile profile(String token);

  OIDCIntrospection introspectAccessToken(String token);

  Optional<OIDCAccessToken> getAccessToken(String login, String password);

  String generateAuthorizationUrl(String host, String redirect);
}
