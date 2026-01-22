package fr.avenirsesr.portfolio.security;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service.OIDCClientAuthenticationService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 *
 * <h1>AccessTokenHelper</h1>
 *
 * <p>Description: used to generate access token for unit tests.
 *
 * <h2>Version:</h2>
 *
 * 1.0
 *
 * <h2>Author:</h2>
 *
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 *
 * 18 Sept 2024
 */
@Component
public class AccessTokenHelper {

  /** Authentication service used to retrieve access token. */
  @Autowired private OIDCClientAuthenticationService authenticationService;

  /** Access token repository. */
  private static final Map<String, String> accessTokenRepository = new HashMap<String, String>();

  /**
   * Provides an access token for a login and a password.
   *
   * @param login The login.
   * @param password The password.
   * @return An access token associated to the user.
   * @throws Exception If the access token cannot be retrieved.
   */
  public String provideAccessToken(String login, String password) throws Exception {

    if (!AccessTokenHelper.accessTokenRepository.containsKey(login)) {

      Optional<OIDCAccessToken> accessToken = authenticationService.getAccessToken(login, password);
      if (accessToken.isEmpty()) {
        throw new RuntimeException("Unable to retrieve access token response for login: " + login);
      }
      AccessTokenHelper.accessTokenRepository.put(login, accessToken.get().accessToken());
    }
    return AccessTokenHelper.accessTokenRepository.get(login);
  }
}
