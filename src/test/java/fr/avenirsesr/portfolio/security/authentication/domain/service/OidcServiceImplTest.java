package fr.avenirsesr.portfolio.security.authentication.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCProfile;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.OidcAuthenticationPort;
import fr.avenirsesr.portfolio.security.principal.domain.exception.PrincipalNotFoundException;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.port.input.PrincipalService;
import java.util.Map;
import java.util.Optional;
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
class OidcServiceImplTest {

  private static final String LOGIN = "login";
  private static final String PASSWORD = "password";
  private static final String HOST = "localhost";
  private static final String CODE = "code";
  private static final String CODE_VERIFIER = "code-verifier";
  private static final String CODE_CHALLENGE = "code-challenge";
  private static final String TOKEN = "token";

  @Mock private OidcAuthenticationPort oidcAuthenticationPort;
  @Mock private PrincipalService principalService;

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
        verifyNoMoreInteractions(oidcAuthenticationPort, principalService);
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
        verifyNoMoreInteractions(oidcAuthenticationPort, principalService);
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
        verifyNoMoreInteractions(oidcAuthenticationPort, principalService);
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
        verifyNoMoreInteractions(oidcAuthenticationPort, principalService);
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
        private UUID userId;
        private OIDCIntrospection result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("token is active and principal exists");

          userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

          OIDCIntrospection introspection = new OIDCIntrospection(TOKEN, true, "user", null);
          Principal principal = new Principal(null, "user", "OIDC", "user", userId, Set.of());

          when(oidcAuthenticationPort.introspectAccessToken(TOKEN)).thenReturn(introspection);
          when(principalService.getPrincipalByProviderAndExternalId("OIDC", "user"))
              .thenReturn(Optional.of(principal));

          result = service.introspectAccessToken(TOKEN);
        }

        @Test
        void thenItShouldReturnIntrospectionWithUserId() {
          BddLogger.then("it should return introspection with user id");

          assertEquals(new OIDCIntrospection(TOKEN, true, "user", userId), result);

          verify(oidcAuthenticationPort).introspectAccessToken(TOKEN);
          verify(principalService).getPrincipalByProviderAndExternalId("OIDC", "user");
          verifyNoMoreInteractions(oidcAuthenticationPort, principalService);
        }
      }

      @Nested
      class AndTokenIsInactive {
        private OIDCIntrospection introspection;
        private OIDCIntrospection result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("token is inactive");

          introspection = new OIDCIntrospection(TOKEN, false, "user", null);

          when(oidcAuthenticationPort.introspectAccessToken(TOKEN)).thenReturn(introspection);

          result = service.introspectAccessToken(TOKEN);
        }

        @Test
        void thenItShouldReturnIntrospectionWithoutPrincipalLookup() {
          BddLogger.then("it should return introspection without principal lookup");

          assertEquals(introspection, result);

          verify(oidcAuthenticationPort).introspectAccessToken(TOKEN);
          verifyNoInteractions(principalService);
          verifyNoMoreInteractions(oidcAuthenticationPort);
        }
      }

      @Nested
      class AndTokenIsActiveButPrincipalIsMissing {
        private PrincipalNotFoundException exception;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("token is active but principal is missing");

          OIDCIntrospection introspection =
              new OIDCIntrospection(TOKEN, true, "unknown-user", null);

          when(oidcAuthenticationPort.introspectAccessToken(TOKEN)).thenReturn(introspection);
          when(principalService.getPrincipalByProviderAndExternalId("OIDC", "unknown-user"))
              .thenReturn(Optional.empty());

          exception =
              assertThrows(
                  PrincipalNotFoundException.class, () -> service.introspectAccessToken(TOKEN));
        }

        @Test
        void thenItShouldThrowPrincipalNotFoundException() {
          BddLogger.then("it should throw principal not found exception");

          assertEquals("No principal found for external id: unknown-user", exception.getMessage());

          verify(oidcAuthenticationPort).introspectAccessToken(TOKEN);
          verify(principalService).getPrincipalByProviderAndExternalId("OIDC", "unknown-user");
          verifyNoMoreInteractions(oidcAuthenticationPort, principalService);
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
        verifyNoMoreInteractions(oidcAuthenticationPort, principalService);
      }
    }
  }

  private OIDCAccessToken accessToken() {
    return new OIDCAccessToken(
        "access-token", "refresh-token", "Bearer", 3600, "openid", null, Map.of(), false);
  }
}
