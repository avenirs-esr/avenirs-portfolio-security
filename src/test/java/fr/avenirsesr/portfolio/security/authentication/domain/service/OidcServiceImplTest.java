package fr.avenirsesr.portfolio.security.authentication.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
import fr.avenirsesr.portfolio.security.authentication.domain.exception.UnauthenticatedSessionException;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCProfile;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.OidcAuthenticationPort;
import fr.avenirsesr.portfolio.security.principal.domain.exception.PrincipalNotFoundException;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OidcServiceImplTest {

  private static final String LOGIN = "login";
  private static final String PASSWORD = "password";
  private static final String HOST = "localhost";
  private static final String CODE = "code";
  private static final String CODE_VERIFIER = "code-verifier";
  private static final String CODE_CHALLENGE = "code-challenge";
  private static final String TOKEN = "token";

  @Mock private OidcAuthenticationPort oidcAuthenticationPort;

  @InjectMocks private OidcServiceImpl service;

  @Nested
  class GivenOidcService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("an OIDC service");
    }

    @Nested
    class WhenGettingAccessToken {
      private OIDCAccessToken expected;
      private Optional<OIDCAccessToken> result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting access token");

        expected = accessToken();

        when(oidcAuthenticationPort.getAccessToken(LOGIN, PASSWORD))
            .thenReturn(Optional.of(expected));

        result = service.getAccessToken(LOGIN, PASSWORD);
      }

      @Test
      void thenItShouldDelegateToPort() {
        BddLogger.then("it should delegate to port");

        assertTrue(result.isPresent());
        assertEquals(expected, result.get());

        verify(oidcAuthenticationPort).getAccessToken(LOGIN, PASSWORD);
        verifyNoMoreInteractions(oidcAuthenticationPort);
      }
    }

    @Nested
    class WhenExchangingAuthorizationCodeForToken {
      private OIDCAccessToken expected;
      private OIDCAccessToken result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("exchanging authorization code for token");

        expected = accessToken();

        when(oidcAuthenticationPort.exchangeAuthorizationCodeForToken(HOST, CODE, CODE_VERIFIER))
            .thenReturn(expected);

        result = service.exchangeAuthorizationCodeForToken(HOST, CODE, CODE_VERIFIER);
      }

      @Test
      void thenItShouldDelegateToPort() {
        BddLogger.then("it should delegate to port");

        assertEquals(expected, result);

        verify(oidcAuthenticationPort).exchangeAuthorizationCodeForToken(HOST, CODE, CODE_VERIFIER);
        verifyNoMoreInteractions(oidcAuthenticationPort);
      }
    }

    @Nested
    class WhenGeneratingServiceURL {
      private String expected;
      private String result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("generating service URL");

        expected = "https://service/callback";

        when(oidcAuthenticationPort.generateServiceURL(HOST)).thenReturn(expected);

        result = service.generateServiceURL(HOST);
      }

      @Test
      void thenItShouldDelegateToPort() {
        BddLogger.then("it should delegate to port");

        assertEquals(expected, result);

        verify(oidcAuthenticationPort).generateServiceURL(HOST);
        verifyNoMoreInteractions(oidcAuthenticationPort);
      }
    }

    @Nested
    class WhenGeneratingAuthorizationUrl {
      private String redirect;
      private String expected;
      private String result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("generating authorization URL");

        redirect = "/cofolio/student";
        expected = "https://dev.avenirs-esr.fr/cas/oidc/oidcAuthorize";

        when(oidcAuthenticationPort.generateAuthorizationUrl(
                "dev.avenirs-esr.fr", redirect, CODE_CHALLENGE))
            .thenReturn(expected);

        result = service.generateAuthorizationUrl("dev.avenirs-esr.fr", redirect, CODE_CHALLENGE);
      }

      @Test
      void thenItShouldDelegateToPort() {
        BddLogger.then("it should delegate to port");

        assertEquals(expected, result);

        verify(oidcAuthenticationPort)
            .generateAuthorizationUrl("dev.avenirs-esr.fr", redirect, CODE_CHALLENGE);
        verifyNoMoreInteractions(oidcAuthenticationPort);
      }
    }

    @Nested
    class WhenIntrospectingAccessToken {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("introspecting access token");
      }

      @Nested
      class AndTokenIsActiveAndPrincipalExists {
        private OIDCIntrospection result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("token is active and principal exists");

          OIDCIntrospection introspection = new OIDCIntrospection(TOKEN, true, "user");
          Principal principal =
              Principal.create(
                  "user@university.com",
                  "user",
                  "OIDC",
                  "user",
                  EUserCategory.STUDENT,
                  EUserStatus.ACTIVE,
                  Set.of());

          when(oidcAuthenticationPort.introspectAccessToken(TOKEN)).thenReturn(introspection);

          result = service.introspectAccessToken(TOKEN);
        }

        @Test
        void thenItShouldReturnIntrospectionWithid() {
          BddLogger.then("it should return introspection with user id");

          assertEquals(new OIDCIntrospection(TOKEN, true, "user"), result);

          verify(oidcAuthenticationPort).introspectAccessToken(TOKEN);
          verifyNoMoreInteractions(oidcAuthenticationPort);
        }
      }

      @Nested
      class AndTokenIsInactive {
        private OIDCIntrospection introspection;
        private OIDCIntrospection result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("token is inactive");

          introspection = new OIDCIntrospection(TOKEN, false, "user");

          when(oidcAuthenticationPort.introspectAccessToken(TOKEN)).thenReturn(introspection);

          result = service.introspectAccessToken(TOKEN);
        }

        @Test
        void thenItShouldReturnIntrospectionWithoutPrincipalLookup() {
          BddLogger.then("it should return introspection without principal lookup");

          assertEquals(introspection, result);

          verify(oidcAuthenticationPort).introspectAccessToken(TOKEN);
          verifyNoMoreInteractions(oidcAuthenticationPort);
        }
      }

      @Nested
      class AndTokenIsActiveButPrincipalIsMissing {
        private PrincipalNotFoundException exception;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("token is active but principal is missing");

          OIDCIntrospection introspection = new OIDCIntrospection(TOKEN, true, "unknown-user");

          when(oidcAuthenticationPort.introspectAccessToken(TOKEN)).thenReturn(introspection);

          exception =
              assertThrows(
                  PrincipalNotFoundException.class, () -> service.introspectAccessToken(TOKEN));
        }
      }
    }

    @Nested
    class WhenGettingProfile {
      private OIDCProfile expected;
      private OIDCProfile result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting profile");

        expected = new OIDCProfile("id", "service", "first", "last", "email@d.tld");

        when(oidcAuthenticationPort.profile(TOKEN)).thenReturn(expected);

        result = service.profile(TOKEN);
      }

      @Test
      void thenItShouldDelegateToPort() {
        BddLogger.then("it should delegate to port");

        assertEquals(expected, result);

        verify(oidcAuthenticationPort).profile(TOKEN);
        verifyNoMoreInteractions(oidcAuthenticationPort);
      }
    }
  }

  @Nested
  class WhenRefreshingSessionIfNeeded {

    @Nested
    class AndAccessTokenIsStillValid {
      private OIDCSession session;
      private OIDCSession result;

      @BeforeEach
      void setupAnd() {
        BddLogger.when("refreshing session if needed");
        BddLogger.and("access token is still valid");

        session =
            new OIDCSession(
                "access-token", "refresh-token", "id-token", Instant.now().plusSeconds(120));

        result = service.refreshSessionIfNeeded(session);
      }

      @Test
      void thenItShouldReturnSameSessionWithoutRefreshingToken() {
        BddLogger.then("it should return same session without refreshing token");

        assertEquals(session, result);

        verify(oidcAuthenticationPort, never()).refreshAccessToken("refresh-token");
        verifyNoMoreInteractions(oidcAuthenticationPort);
      }
    }

    @Nested
    class AndAccessTokenIsExpired {
      private OIDCSession session;
      private OIDCAccessToken refreshedToken;
      private OIDCSession result;

      @BeforeEach
      void setupAnd() {
        BddLogger.when("refreshing session if needed");
        BddLogger.and("access token is expired");

        session =
            new OIDCSession(
                "old-access-token",
                "old-refresh-token",
                "old-id-token",
                Instant.now().minusSeconds(1));

        refreshedToken =
            new OIDCAccessToken(
                "new-access-token",
                "new-refresh-token",
                "Bearer",
                3600,
                "openid",
                "new-id-token",
                Map.of(),
                false);

        when(oidcAuthenticationPort.refreshAccessToken("old-refresh-token"))
            .thenReturn(refreshedToken);

        result = service.refreshSessionIfNeeded(session);
      }

      @Test
      void thenItShouldReturnRefreshedSession() {
        BddLogger.then("it should return refreshed session");

        assertEquals("new-access-token", result.accessToken());
        assertEquals("new-refresh-token", result.refreshToken());
        assertEquals("new-id-token", result.idToken());
        assertTrue(result.accessTokenExpiresAt().isAfter(Instant.now()));

        verify(oidcAuthenticationPort).refreshAccessToken("old-refresh-token");
        verifyNoMoreInteractions(oidcAuthenticationPort);
      }
    }

    @Nested
    class AndRefreshTokenIsNotReturnedByProvider {
      private OIDCSession result;

      @BeforeEach
      void setupAnd() {
        BddLogger.when("refreshing session if needed");
        BddLogger.and("provider does not return a new refresh token");

        OIDCSession session =
            new OIDCSession(
                "old-access-token",
                "old-refresh-token",
                "old-id-token",
                Instant.now().minusSeconds(1));

        OIDCAccessToken refreshedToken =
            new OIDCAccessToken(
                "new-access-token",
                null,
                "Bearer",
                3600,
                "openid",
                "new-id-token",
                Map.of(),
                false);

        when(oidcAuthenticationPort.refreshAccessToken("old-refresh-token"))
            .thenReturn(refreshedToken);

        result = service.refreshSessionIfNeeded(session);
      }

      @Test
      void thenItShouldKeepPreviousRefreshToken() {
        BddLogger.then("it should keep previous refresh token");

        assertEquals("new-access-token", result.accessToken());
        assertEquals("old-refresh-token", result.refreshToken());
        assertEquals("new-id-token", result.idToken());
        assertTrue(result.accessTokenExpiresAt().isAfter(Instant.now()));

        verify(oidcAuthenticationPort).refreshAccessToken("old-refresh-token");
        verifyNoMoreInteractions(oidcAuthenticationPort);
      }
    }

    @Nested
    class AndRefreshTokenIsMissing {
      private OIDCSession session;
      private UnauthenticatedSessionException exception;

      @BeforeEach
      void setupAnd() {
        BddLogger.when("refreshing session if needed");
        BddLogger.and("refresh token is missing");

        session =
            new OIDCSession(
                "expired-access-token", null, "id-token", Instant.now().minusSeconds(1));

        exception =
            assertThrows(
                UnauthenticatedSessionException.class,
                () -> service.refreshSessionIfNeeded(session));
      }

      @Test
      void thenItShouldThrowUnauthenticatedSessionException() {
        BddLogger.then("it should throw unauthenticated session exception");

        assertEquals(UnauthenticatedSessionException.class, exception.getClass());

        verifyNoInteractions(oidcAuthenticationPort);
      }
    }

    @Nested
    class AndRefreshTokenIsBlank {
      private OIDCSession session;
      private UnauthenticatedSessionException exception;

      @BeforeEach
      void setupAnd() {
        BddLogger.when("refreshing session if needed");
        BddLogger.and("refresh token is blank");

        session =
            new OIDCSession(
                "expired-access-token", "   ", "id-token", Instant.now().minusSeconds(1));

        exception =
            assertThrows(
                UnauthenticatedSessionException.class,
                () -> service.refreshSessionIfNeeded(session));
      }

      @Test
      void thenItShouldThrowUnauthenticatedSessionException() {
        BddLogger.then("it should throw unauthenticated session exception");

        assertEquals(UnauthenticatedSessionException.class, exception.getClass());

        verifyNoInteractions(oidcAuthenticationPort);
      }
    }
  }

  private OIDCAccessToken accessToken() {
    return new OIDCAccessToken(
        "access-token", "refresh-token", "Bearer", 3600, "openid", null, Map.of(), false);
  }
}
