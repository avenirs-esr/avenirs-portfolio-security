package fr.avenirsesr.portfolio.security.authentication.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class OidcServiceImplTest {

  @Mock private OidcAuthenticationPort oidcAuthenticationPort;
  @Mock private PrincipalService principalService;

  private OidcServiceImpl service;

  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    service = new OidcServiceImpl(oidcAuthenticationPort, principalService);
  }

  @AfterEach
  void tearDown() throws Exception {
    if (closeable != null) {
      closeable.close();
    }
  }

  @Test
  void getAccessTokenDelegatesToPort() {
    String login = "login";
    String password = "password";

    OIDCAccessToken expected =
        new OIDCAccessToken(
            "access-token", "refresh-token", "Bearer", 3600, "openid", null, Map.of(), false);

    when(oidcAuthenticationPort.getAccessToken(login, password)).thenReturn(Optional.of(expected));

    Optional<OIDCAccessToken> result = service.getAccessToken(login, password);

    assertTrue(result.isPresent());
    assertEquals(expected, result.get());

    verify(oidcAuthenticationPort).getAccessToken(login, password);
    verifyNoMoreInteractions(oidcAuthenticationPort, principalService);
  }

  @Test
  void exchangeAuthorizationCodeForTokenDelegatesToPort() {
    String host = "localhost";
    String code = "code";

    OIDCAccessToken expected =
        new OIDCAccessToken(
            "access-token", "refresh-token", "Bearer", 3600, "openid", null, Map.of(), false);

    when(oidcAuthenticationPort.exchangeAuthorizationCodeForToken(host, code)).thenReturn(expected);

    OIDCAccessToken result = service.exchangeAuthorizationCodeForToken(host, code);

    assertEquals(expected, result);

    verify(oidcAuthenticationPort).exchangeAuthorizationCodeForToken(host, code);
    verifyNoMoreInteractions(oidcAuthenticationPort, principalService);
  }

  @Test
  void generateServiceURLDelegatesToPort() {
    String host = "localhost";
    String expected = "https://service/callback";

    when(oidcAuthenticationPort.generateServiceURL(host)).thenReturn(expected);

    String result = service.generateServiceURL(host);

    assertEquals(expected, result);

    verify(oidcAuthenticationPort).generateServiceURL(host);
    verifyNoMoreInteractions(oidcAuthenticationPort, principalService);
  }

  @Test
  void generateAuthorizationUrlDelegatesToPort() {
    String host = "dev.avenirs-esr.fr";
    String redirect = "/cofolio/student";
    String expected = "https://dev.avenirs-esr.fr/cas/oidc/oidcAuthorize";

    when(oidcAuthenticationPort.generateAuthorizationUrl(host, redirect)).thenReturn(expected);

    String result = service.generateAuthorizationUrl(host, redirect);

    assertEquals(expected, result);

    verify(oidcAuthenticationPort).generateAuthorizationUrl(host, redirect);
    verifyNoMoreInteractions(oidcAuthenticationPort, principalService);
  }

  @Test
  void introspectAccessTokenWithActiveTokenReturnsIntrospectionWithUserId() {
    String token = "token";
    UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

    OIDCIntrospection introspection = new OIDCIntrospection(token, true, "user", null);
    Principal principal = new Principal(null, "user", "OIDC", "user", userId, Set.of());

    when(oidcAuthenticationPort.introspectAccessToken(token)).thenReturn(introspection);
    when(principalService.getPrincipalByProviderAndExternalId("OIDC", "user"))
        .thenReturn(Optional.of(principal));

    OIDCIntrospection result = service.introspectAccessToken(token);

    assertEquals(new OIDCIntrospection(token, true, "user", userId), result);

    verify(oidcAuthenticationPort).introspectAccessToken(token);
    verify(principalService).getPrincipalByProviderAndExternalId("OIDC", "user");
    verifyNoMoreInteractions(oidcAuthenticationPort, principalService);
  }

  @Test
  void introspectAccessTokenWithInactiveTokenReturnsIntrospectionWithoutPrincipalLookup() {
    String token = "token";

    OIDCIntrospection introspection = new OIDCIntrospection(token, false, "user", null);

    when(oidcAuthenticationPort.introspectAccessToken(token)).thenReturn(introspection);

    OIDCIntrospection result = service.introspectAccessToken(token);

    assertEquals(introspection, result);

    verify(oidcAuthenticationPort).introspectAccessToken(token);
    verifyNoInteractions(principalService);
    verifyNoMoreInteractions(oidcAuthenticationPort);
  }

  @Test
  void introspectAccessTokenWithActiveTokenAndMissingPrincipalThrowsPrincipalNotFoundException() {
    String token = "token";

    OIDCIntrospection introspection = new OIDCIntrospection(token, true, "unknown-user", null);

    when(oidcAuthenticationPort.introspectAccessToken(token)).thenReturn(introspection);
    when(principalService.getPrincipalByProviderAndExternalId("OIDC", "unknown-user"))
        .thenReturn(Optional.empty());

    PrincipalNotFoundException exception =
        assertThrows(PrincipalNotFoundException.class, () -> service.introspectAccessToken(token));

    assertEquals("No principal found for external id: unknown-user", exception.getMessage());

    verify(oidcAuthenticationPort).introspectAccessToken(token);
    verify(principalService).getPrincipalByProviderAndExternalId("OIDC", "unknown-user");
    verifyNoMoreInteractions(oidcAuthenticationPort, principalService);
  }

  @Test
  void profileDelegatesToPort() {
    String token = "token";
    OIDCProfile expected = new OIDCProfile("id", "service", "first", "last", "email@d.tld");

    when(oidcAuthenticationPort.profile(token)).thenReturn(expected);

    OIDCProfile result = service.profile(token);

    assertEquals(expected, result);

    verify(oidcAuthenticationPort).profile(token);
    verifyNoMoreInteractions(oidcAuthenticationPort, principalService);
  }
}
