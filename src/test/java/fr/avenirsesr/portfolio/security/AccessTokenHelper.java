package fr.avenirsesr.portfolio.security;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service.OIDCClientOidcAuthenticationService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessTokenHelper {

  private final OIDCClientOidcAuthenticationService authenticationService;

  private static final Map<String, String> accessTokenRepository = new HashMap<>();

  public String provideAccessToken(String login, String password) {
    if (!accessTokenRepository.containsKey(login)) {
      Optional<OIDCAccessToken> accessToken = authenticationService.getAccessToken(login, password);

      if (accessToken.isEmpty()) {
        throw new RuntimeException("Unable to retrieve access token response for login: " + login);
      }

      accessTokenRepository.put(login, accessToken.get().accessToken());
    }

    return accessTokenRepository.get(login);
  }

  public void clear() {
    accessTokenRepository.clear();
  }
}
