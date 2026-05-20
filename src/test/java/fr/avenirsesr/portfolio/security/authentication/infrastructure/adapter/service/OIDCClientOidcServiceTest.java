package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OIDCClientOidcServiceTest {

  private static final String HOST = "localhost";
  private static final String REDIRECT = "/cofolio/student";
  private static final String CODE = "code";
  private static final String CODE_VERIFIER = "code-verifier";
  private static final String CODE_CHALLENGE = "code-challenge";
  private static final String TOKEN = "TEST_ACCESS_TOKEN";
  private static final String REFRESH_TOKEN = "TEST_REFRESH_TOKEN";
  private static final String NEW_ACCESS_TOKEN = "TEST_NEW_ACCESS_TOKEN";

  private static final String USER_LOGIN = "deman";
  private static final String USER_PASSWORD = "password";
  private static final String USER_EMAIL = "deman@univ.fr";
  private static final String USER_FIRST_NAME = "arnaud";
  private static final String USER_LAST_NAME = "DEMAN";

  private static final String CLIENT_ID = "OIDCClientId";
  private static final String CLIENT_SECRET = "OIDCClientSecret";
  private static final String OIDC_SCOPE = "openid profile email offline_access";
  private static final String AUTH_CALLBACK_PUBLIC_PATH = "/security/auth/callback";

  private static final String AUTHORISE_TEMPLATE_URL =
      "https://%s/cas/oidc/authorize?service=%s&code=%s";
  private static final String SERVICE_TEMPLATE_URL = "https://%s/oidc/callback";
  private static final String ACCESS_TOKEN_TEMPLATE_BODY = "username=%s&password=%s";
  private static final String CODE_EXCHANGE_TEMPLATE_BODY =
      "redirect_uri=https://%s/oidc/callback&code=%s&code_verifier=%s";
  private static final String REFRESH_TOKEN_TEMPLATE_BODY =
      "grant_type=refresh_token&refresh_token=%s";

  @Mock private JWTServicePort jwtService;

  private OIDCClientOidcAuthenticationService authenticationService;

  private MockWebServer mockWebServer;

  @BeforeEach
  void setUp() throws Exception {
    mockWebServer = new MockWebServer();
    mockWebServer.setDispatcher(dispatcher());
    mockWebServer.start();

    authenticationService = new OIDCClientOidcAuthenticationService(jwtService);

    ReflectionTestUtils.setField(
        authenticationService, "oidcAuthorizeTemplate", AUTHORISE_TEMPLATE_URL);
    ReflectionTestUtils.setField(
        authenticationService, "oidcAccessTokenBodyTemplate", ACCESS_TOKEN_TEMPLATE_BODY);
    ReflectionTestUtils.setField(
        authenticationService, "oidcCodeExchangeBodyTemplate", CODE_EXCHANGE_TEMPLATE_BODY);
    ReflectionTestUtils.setField(
        authenticationService, "oidcRefreshTokenBodyTemplate", REFRESH_TOKEN_TEMPLATE_BODY);
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
    ReflectionTestUtils.setField(
        authenticationService, "oidcAuthorizeUrl", "https://" + HOST + "/cas/oidc/oidcAuthorize");
    ReflectionTestUtils.setField(authenticationService, "oidcScope", OIDC_SCOPE);
    ReflectionTestUtils.setField(
        authenticationService, "authCallbackPublicPath", AUTH_CALLBACK_PUBLIC_PATH);
  }

  @AfterEach
  void tearDown() throws Exception {
    if (mockWebServer != null) {
      mockWebServer.shutdown();
    }
  }

  @Nested
  class GivenOidcAuthenticationService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("an OIDC authentication service");
    }

    @Nested
    class WhenGeneratingAuthorizeURL {

      @Test
      void thenItShouldReturnExpectedAuthorizeURL() {
        BddLogger.when("generating the legacy authorize URL");
        BddLogger.then("it should match the expected URL");

        assertEquals(
            String.format(AUTHORISE_TEMPLATE_URL, HOST, HOST, CODE),
            authenticationService.generateAuthorizeURL(HOST, CODE));
      }
    }

    @Nested
    class WhenGeneratingAuthorizationUrl {

      @Test
      void thenItShouldReturnExpectedAuthorizationUrlWithPkceParameters() {
        BddLogger.when("generating the authorization URL");
        BddLogger.then("it should include OIDC and PKCE parameters");

        String result =
            authenticationService.generateAuthorizationUrl(HOST, REDIRECT, CODE_CHALLENGE);

        assertThat(result)
            .startsWith("https://localhost/cas/oidc/oidcAuthorize?")
            .contains("client_id=" + CLIENT_ID)
            .contains("response_type=code")
            .contains("scope=openid%20profile%20email%20offline_access")
            .contains("redirect_uri=https://localhost/security/auth/callback")
            .contains("state=/cofolio/student")
            .contains("code_challenge=" + CODE_CHALLENGE)
            .contains("code_challenge_method=S256");
      }
    }

    @Nested
    class WhenGeneratingServiceURL {

      @Test
      void thenItShouldReturnExpectedServiceURL() {
        BddLogger.when("generating the service URL");
        BddLogger.then("it should match the expected URL");

        assertEquals(
            String.format(SERVICE_TEMPLATE_URL, HOST),
            authenticationService.generateServiceURL(HOST));
      }
    }

    @Nested
    class WhenGeneratingCodeExchangeBody {

      @Test
      void thenItShouldReturnExpectedCodeExchangeBody() {
        BddLogger.when("generating the code exchange body");
        BddLogger.then("it should include redirect uri, code and PKCE code verifier");

        assertEquals(
            String.format(CODE_EXCHANGE_TEMPLATE_BODY, HOST, CODE, CODE_VERIFIER),
            authenticationService.generateCodeExchangeBody(HOST, CODE, CODE_VERIFIER));
      }
    }

    @Nested
    class WhenExchangingAuthorizationCodeForToken {

      @Test
      void thenItShouldReturnAccessTokenResponse() {
        BddLogger.when("exchanging an authorization code for a token");

        OIDCAccessToken response =
            authenticationService.exchangeAuthorizationCodeForToken(HOST, CODE, CODE_VERIFIER);

        BddLogger.then("it should return an access token response");

        assertNotNull(response);
        assertEquals(TOKEN, response.accessToken());
        assertEquals(REFRESH_TOKEN, response.refreshToken());
      }
    }

    @Nested
    class WhenRefreshingAccessToken {

      @Test
      void thenItShouldReturnRefreshedAccessToken() {
        BddLogger.when("refreshing an access token");

        OIDCAccessToken response = authenticationService.refreshAccessToken(REFRESH_TOKEN);

        BddLogger.then("it should return a refreshed access token");

        assertNotNull(response);
        assertEquals(NEW_ACCESS_TOKEN, response.accessToken());
        assertEquals(REFRESH_TOKEN, response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600, response.expiresIn());
        assertEquals("openid", response.scope());
      }
    }

    @Nested
    class WhenGeneratingProfileURL {

      @Test
      void thenItShouldReturnExpectedProfileURL() {
        BddLogger.when("generating the profile URL");
        BddLogger.then("it should match the expected profile URL");

        String expectedProfileURL =
            "http://localhost:" + mockWebServer.getPort() + "/cas/oidc/profile?token=" + TOKEN;

        assertEquals(expectedProfileURL, authenticationService.generateProfileURL(TOKEN));
      }
    }

    @Nested
    class WhenGeneratingIntrospectURL {

      @Test
      void thenItShouldReturnExpectedIntrospectURL() {
        BddLogger.when("generating the introspect URL");
        BddLogger.then("it should match the expected introspect URL");

        String expectedIntrospectURL =
            "http://localhost:" + mockWebServer.getPort() + "/cas/oidc/introspect?token=" + TOKEN;

        assertEquals(expectedIntrospectURL, authenticationService.generateIntrospectURL(TOKEN));
      }
    }

    @Nested
    class WhenRequestingProfile {

      @Test
      void thenItShouldReturnProfileAttributes() {
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
    }

    @Nested
    class WhenIntrospectingAccessToken {

      @Test
      void thenItShouldReturnActiveIntrospectionResponse() {
        BddLogger.when("requesting an access token and introspecting it");

        Optional<OIDCAccessToken> accessToken =
            authenticationService.getAccessToken(USER_LOGIN, USER_PASSWORD);

        assertTrue(accessToken.isPresent());

        String accessTokenValue = accessToken.get().accessToken();
        OIDCIntrospection response = authenticationService.introspectAccessToken(accessTokenValue);

        BddLogger.then("it should return an active introspection response for the same token");

        assertEquals(accessTokenValue, response.token());
        assertTrue(response.active());
        assertEquals(USER_LOGIN, response.uniqueSecurityName());
      }
    }

    @Nested
    class WhenGettingAccessToken {

      @Nested
      class AndPasswordIsValid {

        @Test
        void thenItShouldReturnAccessToken() {
          BddLogger.when("requesting an access token with valid credentials");

          Optional<OIDCAccessToken> response =
              authenticationService.getAccessToken(USER_LOGIN, USER_PASSWORD);

          BddLogger.then("it should return a non-empty access token response");

          assertTrue(response.isPresent());

          OIDCAccessToken oidcAccessToken = response.get();

          assertNotNull(oidcAccessToken.accessToken());
          assertFalse(oidcAccessToken.accessToken().isEmpty());
          assertEquals(REFRESH_TOKEN, oidcAccessToken.refreshToken());
        }
      }

      @Nested
      class AndPasswordIsInvalid {

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.when("requesting an access token with invalid credentials");

          Optional<OIDCAccessToken> response =
              authenticationService.getAccessToken(USER_LOGIN, USER_PASSWORD + "false");

          BddLogger.then("it should return empty");

          assertTrue(response.isEmpty());
        }
      }

      @Nested
      class AndProviderReturnsEmptyBody {

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.when("requesting an access token when provider returns an empty body");

          Optional<OIDCAccessToken> response =
              authenticationService.getAccessToken(USER_LOGIN, "null-response");

          BddLogger.then("it should return empty");

          assertTrue(response.isEmpty());
        }
      }

      @Nested
      class AndJwtAccessTokenIsEnabled {

        @Test
        void thenItShouldReturnAccessTokenWithParsedClaims() {
          BddLogger.when("requesting an access token with JWT access token enabled");

          ReflectionTestUtils.setField(authenticationService, "jwtAccessToken", true);

          Map<String, Object> claims = Map.of("k", "v");
          when(jwtService.parseAndCheckSignature(any(OIDCAccessToken.class)))
              .thenReturn(Optional.of(claims));

          Optional<OIDCAccessToken> response =
              authenticationService.getAccessToken(USER_LOGIN, USER_PASSWORD);

          BddLogger.then("it should return a JWT access token response with parsed claims");

          assertTrue(response.isPresent());
          assertTrue(response.get().jwt());
          assertNotNull(response.get().claims());
          assertEquals("v", response.get().claims().get("k"));
        }

        @Test
        void thenItShouldReturnEmptyWhenClaimsAreEmpty() {
          BddLogger.when(
              "requesting an access token with JWT access token enabled and empty claims");

          ReflectionTestUtils.setField(authenticationService, "jwtAccessToken", true);

          when(jwtService.parseAndCheckSignature(any(OIDCAccessToken.class)))
              .thenReturn(Optional.empty());

          Optional<OIDCAccessToken> response =
              authenticationService.getAccessToken(USER_LOGIN, USER_PASSWORD);

          BddLogger.then("it should return empty");

          assertTrue(response.isEmpty());
        }
      }
    }
  }

  private Dispatcher dispatcher() {
    return new Dispatcher() {
      @NotNull
      @Override
      public MockResponse dispatch(@NotNull RecordedRequest request) {
        String path = request.getPath();

        if (path == null) {
          return new MockResponse().setResponseCode(404);
        }

        if (path.startsWith("/cas/oidc/accessToken")) {
          return accessTokenResponse(request);
        }

        if (path.startsWith("/cas/oidc/profile")) {
          return profileResponse();
        }

        if (path.startsWith("/cas/oidc/introspect")) {
          return introspectResponse();
        }

        return new MockResponse().setResponseCode(404);
      }
    };
  }

  private MockResponse accessTokenResponse(RecordedRequest request) {
    String body = request.getBody().readUtf8();

    if (body.contains("password=") && body.contains("false")) {
      return new MockResponse().setResponseCode(401);
    }

    if (body.contains("password=") && body.contains("null-response")) {
      return jsonResponse("");
    }

    if (body.contains("grant_type=refresh_token")
        && body.contains("refresh_token=" + REFRESH_TOKEN)) {
      return jsonResponse(
          "{\"access_token\":\""
              + NEW_ACCESS_TOKEN
              + "\",\"refresh_token\":\""
              + REFRESH_TOKEN
              + "\",\"token_type\":\"Bearer\",\"expires_in\":3600,\"scope\":\"openid\"}");
    }

    return jsonResponse(
        "{\"access_token\":\""
            + TOKEN
            + "\",\"refresh_token\":\""
            + REFRESH_TOKEN
            + "\",\"token_type\":\"Bearer\",\"expires_in\":3600,\"scope\":\"openid\"}");
  }

  private MockResponse profileResponse() {
    return jsonResponse(
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

  private MockResponse introspectResponse() {
    return jsonResponse(
        "{\"token\":\""
            + TOKEN
            + "\",\"active\":true,\"uniqueSecurityName\":\""
            + USER_LOGIN
            + "\"}");
  }

  private MockResponse jsonResponse(String body) {
    return new MockResponse()
        .setResponseCode(200)
        .addHeader("Content-Type", "application/json")
        .setBody(body);
  }
}
