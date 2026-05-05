package fr.avenirsesr.portfolio.security.authentication.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCProfile;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.AuthenticationPort;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.repository.PrincipalRepository;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AuthenticationServiceImplTest {

  @Mock private AuthenticationPort authenticationPort;
  @Mock private PrincipalRepository principalRepository;

  private AuthenticationServiceImpl service;

  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    service = new AuthenticationServiceImpl(authenticationPort, principalRepository);
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
        new OIDCAccessToken("access-token", "Bearer", 3600, "openid", null, Map.of(), false);
    when(authenticationPort.getAccessToken(login, password)).thenReturn(Optional.of(expected));

    Optional<OIDCAccessToken> result = service.getAccessToken(login, password);

    assertTrue(result.isPresent());
    assertEquals(expected, result.get());
    verify(authenticationPort).getAccessToken(login, password);
    verifyNoMoreInteractions(authenticationPort);
  }

  @Test
  void exchangeAuthorizationCodeForTokenDelegatesToPort() {
    String host = "localhost";
    String code = "code";

    OIDCAccessToken expected =
        new OIDCAccessToken("access-token", "Bearer", 3600, "openid", null, Map.of(), false);
    when(authenticationPort.exchangeAuthorizationCodeForToken(host, code)).thenReturn(expected);

    OIDCAccessToken result = service.exchangeAuthorizationCodeForToken(host, code);

    assertEquals(expected, result);
    verify(authenticationPort).exchangeAuthorizationCodeForToken(host, code);
    verifyNoMoreInteractions(authenticationPort);
  }

  @Test
  void generateServiceURLDelegatesToPort() {
    String host = "localhost";
    String expected = "https://service/callback";

    when(authenticationPort.generateServiceURL(host)).thenReturn(expected);

    String result = service.generateServiceURL(host);

    assertEquals(expected, result);
    verify(authenticationPort).generateServiceURL(host);
    verifyNoMoreInteractions(authenticationPort);
  }

  @Test
  void introspectAccessTokenDelegatesToPort() {
    String token = "token";
    OIDCIntrospection expected = new OIDCIntrospection(token, true, "user", null);

    when(authenticationPort.introspectAccessToken(token)).thenReturn(expected);

    OIDCIntrospection result = service.introspectAccessToken(token);

    assertEquals(expected, result);
    verify(authenticationPort).introspectAccessToken(token);
    verifyNoMoreInteractions(authenticationPort);
  }

  @Test
  void profileDelegatesToPort() {
    String token = "token";
    OIDCProfile expected = new OIDCProfile("id", "service", "first", "last", "email@d.tld");

    when(authenticationPort.profile(token)).thenReturn(expected);

    OIDCProfile result = service.profile(token);

    assertEquals(expected, result);
    verify(authenticationPort).profile(token);
    verifyNoMoreInteractions(authenticationPort);
  }
}
