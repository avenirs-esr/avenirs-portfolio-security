package fr.avenirsesr.portfolio.security.authentication.domain.port.input;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCProfile;
import java.util.Optional;

public interface AuthenticationService {
  Optional<OIDCAccessToken> getAccessToken(String login, String password);

  OIDCAccessToken exchangeAuthorizationCodeForToken(String host, String code);

  String generateServiceURL(String host);

  OIDCIntrospection introspectAccessToken(String token);

  OIDCProfile profile(String token);
}
