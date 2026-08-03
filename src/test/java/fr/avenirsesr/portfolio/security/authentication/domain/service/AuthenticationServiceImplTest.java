package fr.avenirsesr.portfolio.security.authentication.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
import fr.avenirsesr.portfolio.security.authentication.domain.exception.UnauthenticatedSessionException;
import fr.avenirsesr.portfolio.security.authentication.domain.model.AuthContext;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import fr.avenirsesr.portfolio.security.authentication.domain.model.SignedAuthContext;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.AuthContextSigningPort;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

  private static final String HOST = "dev.avenirs-esr.fr";
  private static final String REDIRECT = "/cofolio/student";
  private static final String CODE = "authorization-code";
  private static final String CODE_VERIFIER = "code-verifier";
  private static final String CODE_CHALLENGE = "code-challenge";
  private static final String ACCESS_TOKEN = "access-token";
  private static final String REFRESH_TOKEN = "refresh-token";
  private static final String ID_TOKEN = "id-token";
  private static final String LOGIN = "gribonvald";

  @Mock private OidcService oidcService;
  @Mock private AuthContextSigningPort authContextSigningPort;

  @InjectMocks private AuthenticationServiceImpl service;

  @Nested
  class GivenAuthenticationService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("an authentication service");
    }

    @Nested
    class WhenGeneratingAuthorizationUrl {
      private String expected;
      private String result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("generating authorization URL");

        expected = "https://dev.avenirs-esr.fr/cas/oidc/oidcAuthorize";

        when(oidcService.generateAuthorizationUrl(HOST, REDIRECT, CODE_CHALLENGE))
            .thenReturn(expected);

        result = service.generateAuthorizationUrl(HOST, REDIRECT, CODE_CHALLENGE);
      }

      @Test
      void thenItShouldDelegateToOidcService() {
        BddLogger.then("it should delegate to OIDC service");

        assertEquals(expected, result);

        verify(oidcService).generateAuthorizationUrl(HOST, REDIRECT, CODE_CHALLENGE);
        verifyNoMoreInteractions(oidcService, authContextSigningPort);
      }
    }

    @Nested
    class WhenCreatingSessionFromAuthorizationCode {
      private OIDCSession result;
      private Instant before;
      private Instant after;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("creating session from authorization code");

        when(oidcService.exchangeAuthorizationCodeForToken(HOST, CODE, CODE_VERIFIER))
            .thenReturn(accessToken());

        before = Instant.now();
        result = service.createSessionFromAuthorizationCode(HOST, CODE, CODE_VERIFIER);
        after = Instant.now();
      }

      @Test
      void thenItShouldReturnOidcSession() {
        BddLogger.then("it should return OIDC session");

        assertEquals(ACCESS_TOKEN, result.accessToken());
        assertEquals(REFRESH_TOKEN, result.refreshToken());
        assertEquals(ID_TOKEN, result.idToken());

        assertFalse(result.accessTokenExpiresAt().isBefore(before.plusSeconds(3600)));
        assertFalse(result.accessTokenExpiresAt().isAfter(after.plusSeconds(3600)));

        verify(oidcService).exchangeAuthorizationCodeForToken(HOST, CODE, CODE_VERIFIER);
        verifyNoMoreInteractions(oidcService, authContextSigningPort);
      }
    }

    @Nested
    class WhenRefreshingSessionIfNeeded {
      private OIDCSession oidcSession;
      private OIDCSession refreshedSession;
      private OIDCSession result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("refreshing session if needed");

        oidcSession = oidcSession();
        refreshedSession =
            new OIDCSession(
                "new-access-token", REFRESH_TOKEN, ID_TOKEN, Instant.parse("2026-05-13T14:30:00Z"));

        when(oidcService.refreshSessionIfNeeded(oidcSession)).thenReturn(refreshedSession);

        result = service.refreshSessionIfNeeded(oidcSession);
      }

      @Test
      void thenItShouldDelegateToOidcService() {
        BddLogger.then("it should delegate to OIDC service");

        assertEquals(refreshedSession, result);

        verify(oidcService).refreshSessionIfNeeded(oidcSession);
        verifyNoMoreInteractions(oidcService, authContextSigningPort);
      }
    }

    @Nested
    class WhenGettingSignedAuthenticatedContext {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting signed authenticated context");
      }

      @Nested
      class AndSessionIsActiveAndPrincipalExists {
        private UUID principalId;
        private AuthContext expectedAuthContext;
        private SignedAuthContext expectedSignedAuthContext;
        private SignedAuthContext result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("session is active and principal exists");

          principalId = UUID.fromString("00000000-0000-0000-0000-000000000101");

          OIDCIntrospection introspection = new OIDCIntrospection(ACCESS_TOKEN, true, LOGIN);

          Principal principal =
              Principal.toDomain(
                  principalId,
                  Instant.parse("2026-01-01T00:00:00Z"),
                  Instant.parse("2026-01-01T00:00:00Z"),
                  "gribonvald@university.com",
                  LOGIN,
                  "OIDC",
                  LOGIN,
                  EUserCategory.STUDENT,
                  EUserStatus.ACTIVE,
                  Set.of());

          expectedAuthContext = new AuthContext(true, LOGIN);
          expectedSignedAuthContext =
              new SignedAuthContext(
                  "{\"sub\":\"00000000-0000-0000-0000-000000000101\",\"iat\":1,\"exp\":301}",
                  "signature");

          when(oidcService.introspectAccessToken(ACCESS_TOKEN)).thenReturn(introspection);
          when(authContextSigningPort.sign(expectedAuthContext))
              .thenReturn(expectedSignedAuthContext);

          result = service.getSignedAuthenticatedContext(oidcSession());
        }

        @Test
        void thenItShouldReturnSignedAuthContext() {
          BddLogger.then("it should return signed auth context");

          assertEquals(expectedSignedAuthContext, result);

          verify(oidcService).introspectAccessToken(ACCESS_TOKEN);
          verify(authContextSigningPort).sign(expectedAuthContext);
          verifyNoMoreInteractions(oidcService, authContextSigningPort);
        }
      }

      @Nested
      class AndIntrospectionIsNull {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("introspection is null");

          when(oidcService.introspectAccessToken(ACCESS_TOKEN)).thenReturn(null);
        }

        @Test
        void thenItShouldThrowUnauthenticatedSessionException() {
          BddLogger.then("it should throw unauthenticated session exception");

          assertThrows(
              UnauthenticatedSessionException.class,
              () -> service.getSignedAuthenticatedContext(oidcSession()));

          verify(oidcService).introspectAccessToken(ACCESS_TOKEN);
          verifyNoInteractions(authContextSigningPort);
          verifyNoMoreInteractions(oidcService);
        }
      }

      @Nested
      class AndTokenIsInactive {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("token is inactive");

          when(oidcService.introspectAccessToken(ACCESS_TOKEN))
              .thenReturn(new OIDCIntrospection(ACCESS_TOKEN, false, LOGIN));
        }

        @Test
        void thenItShouldThrowUnauthenticatedSessionException() {
          BddLogger.then("it should throw unauthenticated session exception");

          assertThrows(
              UnauthenticatedSessionException.class,
              () -> service.getSignedAuthenticatedContext(oidcSession()));

          verify(oidcService).introspectAccessToken(ACCESS_TOKEN);
          verifyNoInteractions(authContextSigningPort);
          verifyNoMoreInteractions(oidcService);
        }
      }

      @Nested
      class AndPrincipalIsMissing {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("principal is missing");

          when(oidcService.introspectAccessToken(ACCESS_TOKEN))
              .thenReturn(new OIDCIntrospection(ACCESS_TOKEN, true, "unknown-user"));
        }

        @Test
        void thenItShouldThrowUnauthenticatedSessionException() {
          BddLogger.then("it should throw unauthenticated session exception");
          when(oidcService.introspectAccessToken(ACCESS_TOKEN))
              .thenReturn(new OIDCIntrospection(ACCESS_TOKEN, false, "unknown-user"));
          assertThrows(
              UnauthenticatedSessionException.class,
              () -> service.getSignedAuthenticatedContext(oidcSession()));

          verify(oidcService).introspectAccessToken(ACCESS_TOKEN);
          verifyNoInteractions(authContextSigningPort);
          verifyNoMoreInteractions(oidcService);
        }
      }
    }
  }

  private OIDCAccessToken accessToken() {
    return new OIDCAccessToken(
        ACCESS_TOKEN,
        REFRESH_TOKEN,
        "Bearer",
        3600,
        "openid profile email",
        ID_TOKEN,
        Map.of(),
        false);
  }

  private OIDCSession oidcSession() {
    return new OIDCSession(
        ACCESS_TOKEN, REFRESH_TOKEN, ID_TOKEN, Instant.parse("2026-05-13T13:30:00Z"));
  }
}
