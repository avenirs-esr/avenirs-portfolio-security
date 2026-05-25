/** */
package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service;

import static fr.avenirsesr.portfolio.common.utils.RedirectUtils.toSafeHost;

import fr.avenirsesr.portfolio.security.authentication.domain.exception.UnauthenticatedSessionException;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCProfile;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.JWTServicePort;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.OidcAuthenticationPort;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.mapper.OIDCAccessTokenMapper;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.mapper.OIDCIntrospectionMapper;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.mapper.OIDCProfileMapper;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.model.OIDCAccessTokenResponse;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.model.OIDCIntrospectResponse;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.model.OIDCProfileResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class OIDCClientOidcAuthenticationService implements OidcAuthenticationPort {

  /** Rest client to interact with OIDC provider. */
  private final RestClient restClient = RestClient.create();

  private final JWTServicePort jwtService;

  /** Template to generate the OIDC authorization URL. */
  @Value("${avenirs.authentication.oidc.authorise.template.url}")
  private String oidcAuthorizeTemplate;

  /** Template to generate the Access Token query body. */
  @Value("${avenirs.authentication.oidc.token.template.body}")
  private String oidcAccessTokenBodyTemplate;

  /** Template to generate the Access Token query body. */
  @Value("${avenirs.authentication.oidc.code.exchange.template.body}")
  private String oidcCodeExchangeBodyTemplate;

  /** Access Token URL. */
  @Value("${avenirs.authentication.oidc.token.url}")
  private String oidcAccessTokenURL;

  @Value("${avenirs.authentication.oidc.provider.introspect.url}")
  private String oidcProviderIntrospectURL;

  /** Template to generate the service URL. */
  @Value("${avenirs.authentication.service.template}")
  private String serviceTemplate;

  /** Basic authentication header for the interaction with the OIDC provider. */
  private String basicAuthenticationHeader;

  /** Profile end point. */
  @Value("${avenirs.authentication.oidc.provider.profile.url}")
  private String oidcProviderProfileURL;

  @Value("${avenirs.authentication.oidc.client.id}")
  private String clientId;

  @Value("${avenirs.authentication.oidc.client.secret}")
  private String clientSecret;

  @Value("${avenirs.authentication.oidc.token.is.jwt}")
  private boolean jwtAccessToken;

  @Value("${avenirs.authentication.oidc.authorize.url}")
  private String oidcAuthorizeUrl;

  @Value("${avenirs.authentication.oidc.scope}")
  private String oidcScope;

  @Value("${avenirs.authentication.auth.callback.public-path}")
  private String authCallbackPublicPath;

  @Value("${avenirs.authentication.oidc.refresh.template.body}")
  private String oidcRefreshTokenBodyTemplate;

  public OIDCClientOidcAuthenticationService(JWTServicePort jwtService) {
    this.jwtService = jwtService;
  }

  /**
   * Generates the OIDC Authorize URL.
   *
   * @param host The host associated to the OIDC provider.
   * @param code The code provided by the OIDC provider.
   * @return The authorize URL.
   */
  @Override
  public String generateAuthorizeURL(String host, String code) {

    String oidcAuthorizeURL = String.format(oidcAuthorizeTemplate, host, host, code);
    if (log.isTraceEnabled()) {
      String maskedCode = code == null ? "null" : "*".repeat(code.length());
      String maskedOIDCAuthorizeURL = String.format(oidcAuthorizeTemplate, host, host, maskedCode);
      log.trace("generateAuthorizeURL, host: {}", host);
      log.trace("generateAuthorizeURL, code: {}", maskedCode);

      log.trace("generateAuthorizeURL, oidcAuthorizeURL: {}", maskedOIDCAuthorizeURL);
    }
    return oidcAuthorizeURL;
  }

  /**
   * Generates the OIDC Access Token Body query.
   *
   * @param login The user login.
   * @param password The user password.
   * @return The access token body.
   */
  public String generateAccessTokenBody(String login, String password) {
    String oidcAccessTokenBody = String.format(oidcAccessTokenBodyTemplate, login, password);

    if (log.isDebugEnabled()) {
      String maskedPassword = password == null ? "null" : "*".repeat(password.length());
      String maskedOIDCAccessTokenBody = String.format(oidcAccessTokenBody, login, maskedPassword);
      log.debug(
          "generateAccessTokenBody, maskedOIDCAccessTokenBody: {}", maskedOIDCAccessTokenBody);
    }
    return oidcAccessTokenBody;
  }

  /**
   * Generates the OIDC code exchange Body query.
   *
   * @param host The host associated to the service (for redirect_uri).
   * @param code The code given by the oidc provider.
   * @param codeVerifier The PKCE code verifier used for the code exchange.
   * @return The exchange query body.
   */
  public String generateCodeExchangeBody(String host, String code, String codeVerifier) {
    String safeHost = toSafeHost(host);

    String oidcCodeExchangeBody =
        String.format(oidcCodeExchangeBodyTemplate, safeHost, code, codeVerifier);

    if (log.isDebugEnabled()) {
      String maskedCode = code == null ? "null" : "*".repeat(code.length());
      String maskedCodeVerifier = codeVerifier == null ? "null" : "*".repeat(codeVerifier.length());

      String maskedOIDCCodeExchangeBody =
          String.format(oidcCodeExchangeBodyTemplate, safeHost, maskedCode, maskedCodeVerifier);

      log.debug(
          "generateCodeExchangeBody, maskedOIDCCodeExchangeBody: {}", maskedOIDCCodeExchangeBody);
    }

    return oidcCodeExchangeBody;
  }

  /**
   * Generates the OIDC Authorize URL.
   *
   * @param host The host associated to the OIDC provider.
   * @return The service URL.
   */
  @Override
  public String generateServiceURL(String host) {

    String serviceURL = String.format(serviceTemplate, host);
    log.debug("generateServiceURL, serviceURL: {}", serviceURL);
    return serviceURL;
  }

  /**
   * Generates the OIDC Profile URL.
   *
   * @param token The access token used to retrieve the user profile.
   * @return The profile URL
   */
  @Override
  public String generateProfileURL(String token) {

    String profileURL = String.format(oidcProviderProfileURL, token);
    if (log.isDebugEnabled()) {
      String maskedProfileURL =
          String.format(
              oidcProviderProfileURL,
              token.substring(0, 4) + "****" + token.substring(token.length() - 4));
      log.debug("generateProfileURL, maskedProfileURL: {}", maskedProfileURL);
    }
    return profileURL;
  }

  /**
   * Generates the OIDC Introspect URL.
   *
   * @param token The access token used to retrieve the user profile.
   * @return The introspect URL
   */
  @Override
  public String generateIntrospectURL(String token) {

    String introspectURL = String.format(oidcProviderIntrospectURL, token);
    if (log.isTraceEnabled()) {
      String maskedIntrospectURL =
          String.format(
              oidcProviderIntrospectURL,
              token.substring(0, 4) + "****" + token.substring(token.length() - 4));
      log.trace("generateIntrospectURL, maskedIntrospectURL: {}", maskedIntrospectURL);
    }

    return introspectURL;
  }

  @Override
  public OIDCAccessToken exchangeAuthorizationCodeForToken(
      String host, String code, String codeVerifier) {

    String safeHost = toSafeHost(host);
    String redirectUri = "https://" + safeHost + authCallbackPublicPath;

    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "authorization_code");
    form.add("client_id", clientId);
    form.add("client_secret", clientSecret);
    form.add("redirect_uri", redirectUri);
    form.add("code", code);
    form.add("code_verifier", codeVerifier);

    log.info("Exchanging OIDC code with redirect_uri={}", redirectUri);

    OIDCAccessTokenResponse payload =
        restClient
            .post()
            .uri(oidcAccessTokenURL)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON)
            .body(form)
            .retrieve()
            .body(OIDCAccessTokenResponse.class);

    return OIDCAccessTokenMapper.toDomain(payload);
  }

  /**
   * Gets the profile associated to an access token.
   *
   * @param token The token granted to the user for which the profile has to be retrieved.
   * @return The Profile response of the OIDC Provider.
   */
  @Override
  public OIDCProfile profile(String token) {

    log.trace("profile");

    OIDCProfileResponse payload =
        restClient
            .post()
            .uri(generateProfileURL(token))
            .header("Authorization", basicAuthentication())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(OIDCProfileResponse.class);

    log.debug("profile, payload: {}", payload);

    return OIDCProfileMapper.toDomain(payload);
  }

  /**
   * OIDC introspection of an access token.
   *
   * @param token The access token to introspect.
   * @return The introspect response of the OIDC Provider.
   */
  @Override
  public OIDCIntrospection introspectAccessToken(String token) {

    log.debug("introspectAccessToken");

    OIDCIntrospectResponse payload =
        restClient
            .post()
            .uri(generateIntrospectURL(token))
            .header("Authorization", basicAuthentication())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(OIDCIntrospectResponse.class);

    log.info("introspect, payload: {}", payload);

    return OIDCIntrospectionMapper.toDomain(payload);
  }

  /**
   * Generates an access token
   *
   * @param login The user login.
   * @param password The user password.
   * @return An Optional of OIDCAccessToken
   */
  @Override
  public Optional<OIDCAccessToken> getAccessToken(String login, String password) {
    try {

      String body = generateAccessTokenBody(login, password);

      OIDCAccessTokenResponse payload =
          restClient
              .post()
              .uri(oidcAccessTokenURL)
              .header("Content-Type", "application/x-www-form-urlencoded")
              .body(body)
              .retrieve()
              .body(OIDCAccessTokenResponse.class);

      if (payload == null) {
        return Optional.empty();
      }

      OIDCAccessToken accessToken = OIDCAccessTokenMapper.toDomain(payload);

      log.debug("getAccessToken, jwtAccessToken: {}", jwtAccessToken);

      if (jwtAccessToken) {
        final Optional<Map<String, Object>> claims =
            this.jwtService.parseAndCheckSignature(accessToken);
        if (claims.isEmpty()) {
          log.error("Invalid access token response: {}", payload);
          return Optional.empty();
        }
        log.trace("Claims: {}", claims.get());
        accessToken =
            new OIDCAccessToken(
                accessToken.accessToken(),
                accessToken.refreshToken(),
                accessToken.tokenType(),
                accessToken.expiresIn(),
                accessToken.scope(),
                accessToken.rawIdToken(),
                new HashMap<>(claims.get()),
                true);
      }
      return Optional.of(accessToken);
    } catch (HttpClientErrorException e) {
      log.error(
          "getAccessToken, error while retrieving access token for {}: {}",
          login,
          +e.getStatusCode().value());
      return Optional.empty();
    }
  }

  @Override
  public String generateAuthorizationUrl(String host, String redirect, String codeChallenge) {
    String safeHost = toSafeHost(host);

    String redirectUri =
        UriComponentsBuilder.newInstance()
            .scheme("https")
            .host(safeHost)
            .path(authCallbackPublicPath)
            .build()
            .toUriString();

    return UriComponentsBuilder.fromUriString(oidcAuthorizeUrl)
        .queryParam("client_id", clientId)
        .queryParam("response_type", "code")
        .queryParam("scope", oidcScope)
        .queryParam("redirect_uri", redirectUri)
        .queryParam("state", redirect)
        .queryParam("code_challenge", codeChallenge)
        .queryParam("code_challenge_method", "S256")
        .build()
        .encode()
        .toUriString();
  }

  /**
   * Gives the header for a basic authentication based on the client id and client secret. The final
   * header is generated only once and then reused.
   *
   * @return The basic authentication header.
   */
  private String basicAuthentication() {
    if (basicAuthenticationHeader == null) {

      log.trace("basicAuthentication generating authentication header. ");
      final String auth = clientId + ":" + clientSecret;

      final byte[] encodedAuth =
          Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
      basicAuthenticationHeader = "Basic " + new String(encodedAuth);
    }
    log.trace("basicAuthentication, basicAuthenticationHeader: {}", basicAuthenticationHeader);
    return basicAuthenticationHeader;
  }

  @Override
  public OIDCAccessToken refreshAccessToken(String refreshToken) {
    try {
      String body = String.format(oidcRefreshTokenBodyTemplate, refreshToken);

      OIDCAccessTokenResponse payload =
          restClient
              .post()
              .uri(oidcAccessTokenURL)
              .contentType(MediaType.APPLICATION_FORM_URLENCODED)
              .accept(MediaType.APPLICATION_JSON)
              .body(body)
              .retrieve()
              .body(OIDCAccessTokenResponse.class);

      return OIDCAccessTokenMapper.toDomain(payload);

    } catch (HttpClientErrorException.BadRequest e) {
      throw new UnauthenticatedSessionException();
    }
  }
}
