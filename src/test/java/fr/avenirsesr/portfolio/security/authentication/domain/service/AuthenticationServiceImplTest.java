package fr.avenirsesr.portfolio.security.authentication.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.security.authentication.domain.exception.UnauthenticatedSessionException;
import fr.avenirsesr.portfolio.security.authentication.domain.model.AuthContext;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.OidcService;
import fr.avenirsesr.portfolio.security.principal.domain.model.Principal;
import fr.avenirsesr.portfolio.security.principal.domain.port.input.PrincipalService;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AuthenticationServiceImplTest {

  @Mock private OidcService oidcService;
  @Mock private PrincipalService principalService;

  private AuthenticationServiceImpl service;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    service = new AuthenticationServiceImpl(oidcService, principalService);
  }

  @AfterEach
  void tearDown() throws Exception {
    if (closeable != null) {
      closeable.close();
    }
  }

  @Test
  void generateAuthorizationUrlDelegatesToOidcService() {
    String host = "dev.avenirs-esr.fr";
    String redirect = "/cofolio/student";
    String expected = "https://dev.avenirs-esr.fr/cas/oidc/oidcAuthorize";

    when(oidcService.generateAuthorizationUrl(host, redirect)).thenReturn(expected);

    String result = service.generateAuthorizationUrl(host, redirect);

    assertEquals(expected, result);

    verify(oidcService).generateAuthorizationUrl(host, redirect);
    verifyNoMoreInteractions(oidcService, principalService);
  }

  @Test
  void createSessionFromAuthorizationCodeReturnsOidcSession() {
    String host = "dev.avenirs-esr.fr";
    String code = "authorization-code";

    OIDCAccessToken accessToken =
        new OIDCAccessToken(
            "access-token",
            "refresh-token",
            "Bearer",
            3600,
            "openid profile email",
            "id-token",
            Map.of(),
            false);

    when(oidcService.exchangeAuthorizationCodeForToken(host, code)).thenReturn(accessToken);

    Instant before = Instant.now();

    OIDCSession result = service.createSessionFromAuthorizationCode(host, code);

    Instant after = Instant.now();

    assertEquals("access-token", result.accessToken());
    assertEquals("refresh-token", result.refreshToken());
    assertEquals("id-token", result.idToken());

    assertFalse(result.accessTokenExpiresAt().isBefore(before.plusSeconds(3600)));
    assertFalse(result.accessTokenExpiresAt().isAfter(after.plusSeconds(3600)));

    verify(oidcService).exchangeAuthorizationCodeForToken(host, code);
    verifyNoMoreInteractions(oidcService, principalService);
  }

  @Test
  void getAuthenticatedContextWithActiveSessionReturnsAuthContext() {
    UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000101");

    OIDCSession oidcSession =
        new OIDCSession(
            "access-token", "refresh-token", "id-token", Instant.parse("2026-05-13T13:30:00Z"));

    OIDCIntrospection introspection =
        new OIDCIntrospection("access-token", true, "gribonvald", null);

    Principal principal = new Principal(null, "gribonvald", "OIDC", "gribonvald", userId, Set.of());

    when(oidcService.introspectAccessToken("access-token")).thenReturn(introspection);
    when(principalService.getPrincipalByProviderAndExternalId("OIDC", "gribonvald"))
        .thenReturn(Optional.of(principal));

    AuthContext result = service.getAuthenticatedContext(oidcSession);

    assertEquals(new AuthContext(true, userId, "gribonvald"), result);

    verify(oidcService).introspectAccessToken("access-token");
    verify(principalService).getPrincipalByProviderAndExternalId("OIDC", "gribonvald");
    verifyNoMoreInteractions(oidcService, principalService);
  }

  @Test
  void getAuthenticatedContextWithNullIntrospectionThrowsUnauthenticatedSessionException() {
    OIDCSession oidcSession =
        new OIDCSession(
            "access-token", "refresh-token", "id-token", Instant.parse("2026-05-13T13:30:00Z"));

    when(oidcService.introspectAccessToken("access-token")).thenReturn(null);

    assertThrows(
        UnauthenticatedSessionException.class, () -> service.getAuthenticatedContext(oidcSession));

    verify(oidcService).introspectAccessToken("access-token");
    verifyNoInteractions(principalService);
    verifyNoMoreInteractions(oidcService);
  }

  @Test
  void getAuthenticatedContextWithInactiveTokenThrowsUnauthenticatedSessionException() {
    OIDCSession oidcSession =
        new OIDCSession(
            "access-token", "refresh-token", "id-token", Instant.parse("2026-05-13T13:30:00Z"));

    OIDCIntrospection introspection =
        new OIDCIntrospection("access-token", false, "gribonvald", null);

    when(oidcService.introspectAccessToken("access-token")).thenReturn(introspection);

    assertThrows(
        UnauthenticatedSessionException.class, () -> service.getAuthenticatedContext(oidcSession));

    verify(oidcService).introspectAccessToken("access-token");
    verifyNoInteractions(principalService);
    verifyNoMoreInteractions(oidcService);
  }

  @Test
  void getAuthenticatedContextWithMissingPrincipalThrowsUnauthenticatedSessionException() {
    OIDCSession oidcSession =
        new OIDCSession(
            "access-token", "refresh-token", "id-token", Instant.parse("2026-05-13T13:30:00Z"));

    OIDCIntrospection introspection =
        new OIDCIntrospection("access-token", true, "unknown-user", null);

    when(oidcService.introspectAccessToken("access-token")).thenReturn(introspection);
    when(principalService.getPrincipalByProviderAndExternalId("OIDC", "unknown-user"))
        .thenReturn(Optional.empty());

    assertThrows(
        UnauthenticatedSessionException.class, () -> service.getAuthenticatedContext(oidcSession));

    verify(oidcService).introspectAccessToken("access-token");
    verify(principalService).getPrincipalByProviderAndExternalId("OIDC", "unknown-user");
    verifyNoMoreInteractions(oidcService, principalService);
  }
}
