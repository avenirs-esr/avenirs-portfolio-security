package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCProfile;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.JWTServicePort;
import java.util.Map;
import java.util.Optional;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OIDCClientAuthenticationServiceTest {

  private static final String HOST = "localhost";
  private static final String CODE = "code";
  private static final String TOKEN = "TEST_ACCESS_TOKEN";
  private static final String USER_LOGIN = "deman";
  private static final String USER_PASSWORD = "password";
  private static final String USER_EMAIL = "deman@univ.fr";
  private static final String USER_FIRST_NAME = "arnaud";
  private static final String USER_LAST_NAME = "DEMAN";

  private static final String CLIENT_ID = "OIDCClientId";
  private static final String CLIENT_SECRET = "OIDCClientSecret";

  private static final String AUTHORISE_TEMPLATE_URL =
      "https://%s/cas/oidc/authorize?service=%s&code=%s";
  private static final String SERVICE_TEMPLATE_URL = "https://%s/oidc/callback";
  private static final String ACCESS_TOKEN_TEMPLATE_BODY = "username=%s&password=%s";
  private static final String CODE_EXCHANGE_TEMPLATE_BODY =
      "redirect_uri=https://%s/oidc/callback&code=%s";

  @Mock private JWTServicePort jwtService;

  private OIDCClientAuthenticationService authenticationService;

  private MockWebServer mockWebServer;

  @BeforeEach
  void setUp() throws Exception {
    mockWebServer = new MockWebServer();
    mockWebServer.setDispatcher(
        new Dispatcher() {
          @NotNull
          @Override
          public MockResponse dispatch(@NotNull RecordedRequest request) {
            String path = request.getPath();
            if (path == null) {
              return new MockResponse().setResponseCode(404);
            }
            if (path.startsWith("/cas/oidc/accessToken")) {
              String body = request.getBody().readUtf8();
              if (body.contains("password=") && body.contains("false")) {
                return new MockResponse().setResponseCode(401);
              }
              if (body.contains("password=") && body.contains("null-response")) {
                return new MockResponse()
                    .setResponseCode(200)
                    .addHeader("Content-Type", "application/json")
                    .setBody("");
              }
              return new MockResponse()
                  .setResponseCode(200)
                  .addHeader("Content-Type", "application/json")
                  .setBody(
                      "{\"access_token\":\""
                          + TOKEN
                          + "\",\"token_type\":\"Bearer\",\"expires_in\":3600,\"scope\":\"openid\"}");
            }
            if (path.startsWith("/cas/oidc/profile")) {
              return new MockResponse()
                  .setResponseCode(200)
                  .addHeader("Content-Type", "application/json")
                  .setBody(
                      "{\"id\":\""
                          + USER_LOGIN
                          + "\",\"service\":\""
                          + CLIENT_ID
                          + "\",\"attributes\":{\"given_name\":\""
                          + USER_FIRST_NAME
                          + "\",\"family_name\":\""
                          + USER_LAST_NAME
                          + "\",\"email\":\""
                          + USER_EMAIL
                          + "\"}}");
            }
            if (path.startsWith("/cas/oidc/introspect")) {
              return new MockResponse()
                  .setResponseCode(200)
                  .addHeader("Content-Type", "application/json")
                  .setBody(
                      "{\"token\":\""
                          + TOKEN
                          + "\",\"active\":true,\"uniqueSecurityName\":\""
                          + USER_LOGIN
                          + "\"}");
            }
            return new MockResponse().setResponseCode(404);
          }
        });
    mockWebServer.start();

    authenticationService = new OIDCClientAuthenticationService(jwtService);
    ReflectionTestUtils.setField(
        authenticationService, "oidcAuthorizeTemplate", AUTHORISE_TEMPLATE_URL);
    ReflectionTestUtils.setField(
        authenticationService, "oidcAccessTokenBodyTemplate", ACCESS_TOKEN_TEMPLATE_BODY);
    ReflectionTestUtils.setField(
        authenticationService, "oidcCodeExchangeBodyTemplate", CODE_EXCHANGE_TEMPLATE_BODY);
    ReflectionTestUtils.setField(
        authenticationService,
        "oidcAccessTokenURL",
        "http://localhost:" + mockWebServer.getPort() + "/cas/oidc/accessToken");
    ReflectionTestUtils.setField(
        authenticationService,
        "oidcProviderProfileURL",
        "http://localhost:" + mockWebServer.getPort() + "/cas/oidc/profile?token=%s");
    ReflectionTestUtils.setField(
        authenticationService,
        "oidcProviderIntrospectURL",
        "http://localhost:" + mockWebServer.getPort() + "/cas/oidc/introspect?token=%s");
    ReflectionTestUtils.setField(authenticationService, "serviceTemplate", SERVICE_TEMPLATE_URL);
    ReflectionTestUtils.setField(authenticationService, "clientId", CLIENT_ID);
    ReflectionTestUtils.setField(authenticationService, "clientSecret", CLIENT_SECRET);
    ReflectionTestUtils.setField(authenticationService, "jwtAccessToken", false);
  }

  @AfterEach
  void tearDown() throws Exception {
    if (mockWebServer != null) {
      mockWebServer.shutdown();
    }
  }

  @Test
  void generateAuthorizeURL() {
    BddLogger.given("an OIDC configuration and an expected authorize URL");
    BddLogger.when("generating the authorize URL");
    BddLogger.then("it should match the expected URL");
    assertEquals(
        String.format(AUTHORISE_TEMPLATE_URL, HOST, HOST, CODE),
        authenticationService.generateAuthorizeURL(HOST, CODE),
        "Generated authorize URL.");
  }

  @Test
  void generateServiceURL() {
    BddLogger.given("an OIDC host and an expected service URL");
    BddLogger.when("generating the service URL");
    BddLogger.then("it should match the expected URL");
    assertEquals(
        String.format(SERVICE_TEMPLATE_URL, HOST),
        authenticationService.generateServiceURL(HOST),
        "Generated service URL.");
  }

  @Test
  void generateCodeExchangeBody() {
    BddLogger.given("a code exchange template body and an expected formatted body");
    BddLogger.when("generating the code exchange body");
    BddLogger.then("it should match the expected formatted body");
    assertEquals(
        String.format(CODE_EXCHANGE_TEMPLATE_BODY, HOST, CODE),
        authenticationService.generateCodeExchangeBody(HOST, CODE));
  }

  @Test
  void exchangeAuthorizationCodeForToken() {
    BddLogger.given("a running mock OIDC token endpoint");
    BddLogger.when("exchanging an authorization code for a token");
    OIDCAccessToken response = authenticationService.exchangeAuthorizationCodeForToken(HOST, CODE);

    BddLogger.then("it should return an access token response");
    assertNotNull(response);
    assertEquals(TOKEN, response.accessToken());
  }

  @Test
  void generateProfileURL() {
    BddLogger.given("a running mock OIDC profile endpoint");
    String expectedProfileURL =
        "http://localhost:" + mockWebServer.getPort() + "/cas/oidc/profile?token=" + TOKEN;

    BddLogger.when("generating the profile URL");
    BddLogger.then("it should match the expected profile URL");
    assertEquals(
        expectedProfileURL,
        authenticationService.generateProfileURL(TOKEN),
        "Generated profile URL.");
  }

  @Test
  void generateIntrospectURL() {
    BddLogger.given("a running mock OIDC introspect endpoint");
    String expectedIntrospectURL =
        "http://localhost:" + mockWebServer.getPort() + "/cas/oidc/introspect?token=" + TOKEN;

    BddLogger.when("generating the introspect URL");
    BddLogger.then("it should match the expected introspect URL");
    assertEquals(
        expectedIntrospectURL,
        authenticationService.generateIntrospectURL(TOKEN),
        "Generated introspect URL.");
  }

  @Test
  void profile() throws Exception {
    BddLogger.given("valid user credentials and a running mock OIDC profile endpoint");
    BddLogger.when("requesting the user profile through the authentication service");

    Optional<OIDCAccessToken> accessToken =
        authenticationService.getAccessToken(USER_LOGIN, USER_PASSWORD);
    assertTrue(accessToken.isPresent());
    OIDCProfile response = authenticationService.profile(accessToken.get().accessToken());

    BddLogger.then("it should return profile attributes matching the expected values");
    assertThat(response)
        .extracting(
            OIDCProfile::id, OIDCProfile::firstName, OIDCProfile::lastName, OIDCProfile::email)
        .containsExactly(USER_LOGIN, USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL);
  }

  @Test
  void introspectAccessToken() throws Exception {
    BddLogger.given("valid user credentials and a running mock OIDC introspect endpoint");
    BddLogger.when("requesting an access token and introspecting it");
    Optional<OIDCAccessToken> accessToken =
        authenticationService.getAccessToken(USER_LOGIN, USER_PASSWORD);
    assertTrue(accessToken.isPresent());
    String accessTokenValue = accessToken.get().accessToken();
    OIDCIntrospection response = authenticationService.introspectAccessToken(accessTokenValue);

    BddLogger.then("it should return an active introspection response for the same token");
    assertEquals(accessTokenValue, response.token(), "Access token in response");
    assertTrue(response.active(), "Active flag in response");
    assertEquals(USER_LOGIN, response.uniqueSecurityName(), "Unique security name in response");
  }

  @Test
  void getAccessTokenWithValidPassword() {
    BddLogger.given("valid user credentials");
    BddLogger.when("requesting an access token");

    Optional<OIDCAccessToken> response =
        authenticationService.getAccessToken(USER_LOGIN, USER_PASSWORD);

    BddLogger.then("it should return a non-empty access token response");
    assertFalse(response.isEmpty());
    OIDCAccessToken oidcAccessToken = response.get();
    if (oidcAccessToken.jwt()) {
      assertNotNull(oidcAccessToken.claims(), "Claims in response");
      assertFalse(oidcAccessToken.claims().isEmpty(), "Claims not empty");
    }
    assertNotNull(oidcAccessToken.accessToken(), "Access Token in response");
    assertFalse(oidcAccessToken.accessToken().isEmpty(), "Access Token not empty");
  }

  @Test
  void getAccessTokenWithInvalidPassword() {
    BddLogger.given("invalid user credentials");
    BddLogger.when("requesting an access token");

    Optional<OIDCAccessToken> response =
        authenticationService.getAccessToken(USER_LOGIN, USER_PASSWORD + "false");

    BddLogger.then("it should return empty");
    assertTrue(response.isEmpty());
  }

  @Test
  void getAccessTokenWhenResponseIsNull() {
    BddLogger.given("a token endpoint returning an empty body");
    BddLogger.when("requesting an access token");
    Optional<OIDCAccessToken> response =
        authenticationService.getAccessToken(USER_LOGIN, "null-response");

    BddLogger.then("it should return empty");
    assertTrue(response.isEmpty());
  }

  @Test
  void getAccessTokenWhenJwtAccessTokenIsTrue() {
    BddLogger.given("jwtAccessToken enabled and a JWT service returning claims");
    ReflectionTestUtils.setField(authenticationService, "jwtAccessToken", true);

    Map<String, Object> claims = Map.of("k", "v");
    when(jwtService.parseAndCheckSignature(any(OIDCAccessToken.class)))
        .thenReturn(Optional.of(claims));

    BddLogger.when("requesting an access token");
    Optional<OIDCAccessToken> response =
        authenticationService.getAccessToken(USER_LOGIN, USER_PASSWORD);

    BddLogger.then("it should return a JWT access token response with the parsed claims");
    assertTrue(response.isPresent());
    assertTrue(response.get().jwt());
    assertNotNull(response.get().claims());
    assertEquals("v", response.get().claims().get("k"));
  }

  @Test
  void getAccessTokenWhenJwtAccessTokenIsTrueAndClaimsAreEmpty() {
    BddLogger.given("jwtAccessToken enabled and a JWT service returning empty claims");
    ReflectionTestUtils.setField(authenticationService, "jwtAccessToken", true);

    when(jwtService.parseAndCheckSignature(any(OIDCAccessToken.class)))
        .thenReturn(Optional.empty());

    BddLogger.when("requesting an access token");
    Optional<OIDCAccessToken> response =
        authenticationService.getAccessToken(USER_LOGIN, USER_PASSWORD);

    BddLogger.then("it should return empty");
    assertTrue(response.isEmpty());
  }
}
