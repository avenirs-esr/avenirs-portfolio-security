package fr.avenirsesr.portfolio.security.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.security.AccessTokenHelper;
import fr.avenirsesr.portfolio.security.authentication.application.adapter.AuthenticationController;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.model.OIDCAccessTokenResponse;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.model.OIDCIntrospectResponse;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.model.OIDCProfileResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Disabled("Needs refactoring (tests outside authentication package)")
class AuthenticationControllerTest {

  @Value("${avenirs.test.authentication.controller.user.login}")
  private String userLogin;

  @Value("${avenirs.test.authentication.controller.user.password}")
  private String userPassword;

  @Value("${avenirs.test.authentication.controller.profile.expected.id}")
  private String profileExpectedId;

  @Value("${avenirs.test.authentication.controller.profile.expected.service}")
  private String profileExpectedService;

  @Value("${avenirs.test.authentication.controller.profile.expected.first.name}")
  private String profileExpectedFirstName;

  @Value("${avenirs.test.authentication.controller.profile.expected.last.name}")
  private String profileExpectedLastName;

  @Value("${avenirs.test.authentication.controller.profile.expected.email}")
  private String profileExpectedEmail;

  @Mock private AuthenticationService authenticationService;

  @Mock private HttpServletResponse response;

  @InjectMocks private AuthenticationController authenticationController;

  private AutoCloseable closeable;

  @Autowired AccessTokenHelper accessTokenHelper;

  @Autowired private AuthenticationController notMockedAuthenticationController;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    if (closeable != null) {
      closeable.close();
    }
  }

  @Test
  void oidcCallbackWithHostAndCode() {
    String forwardHost = "test-host.com";
    String code = "testCode";
    OIDCAccessToken expectedAccessToken =
        new OIDCAccessToken("TEST_ACCESS_TOKEN", null, 0, null, null, null, false);

    when(authenticationService.exchangeAuthorizationCodeForToken(forwardHost, code))
        .thenReturn(expectedAccessToken);

    try {
      ResponseEntity<?> responseEntity =
          authenticationController.oidcCallback(forwardHost, response, code);

      assertNotNull(responseEntity);
      assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
      assertInstanceOf(OIDCAccessTokenResponse.class, responseEntity.getBody());
      assertEquals(
          "TEST_ACCESS_TOKEN",
          ((OIDCAccessTokenResponse) responseEntity.getBody()).getAccessToken());
      verify(authenticationService).exchangeAuthorizationCodeForToken(forwardHost, code);
    } catch (IOException e) {
      fail("IOException should not be thrown: " + e.getMessage());
    }
  }

  @Test
  void oidcCallbackWithoutHostAndCode() {

    OIDCAccessToken expectedAccessToken =
        new OIDCAccessToken("TEST_ACCESS_TOKEN", null, 0, null, null, null, false);
    when(authenticationService.exchangeAuthorizationCodeForToken("localhost", null))
        .thenReturn(expectedAccessToken);

    try {
      ResponseEntity<?> responseEntity =
          authenticationController.oidcCallback(null, response, null);

      assertNotNull(responseEntity);
      assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
      assertInstanceOf(OIDCAccessTokenResponse.class, responseEntity.getBody());
      assertEquals(
          "TEST_ACCESS_TOKEN",
          ((OIDCAccessTokenResponse) responseEntity.getBody()).getAccessToken());
      verify(authenticationService).exchangeAuthorizationCodeForToken("localhost", null);
    } catch (IOException e) {
      fail("IOException should not be thrown: " + e.getMessage());
    }
  }

  @Test
  void profileWithValidToken() {
    try {
      String token = accessTokenHelper.provideAccessToken(userLogin, userPassword);
      OIDCProfileResponse response = notMockedAuthenticationController.profile(token);
      assertNotNull(response, "Profile response not nul vor valid token");
      assertEquals(profileExpectedId, response.getId(), "Profile response id");
      assertEquals(profileExpectedService, response.getService(), "Profile response service");
      assertEquals(
          profileExpectedFirstName, response.getFirstName(), "Profile response first name");
      assertEquals(profileExpectedLastName, response.getLastName(), "Profile response last name");
      assertEquals(profileExpectedEmail, response.getEmail(), "Profile response email");

    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }

  @Test
  void profileWithInactiveToken() {
    String token = "inactive-token";
    OIDCIntrospection introspection = new OIDCIntrospection(null, false, null);

    when(authenticationService.introspectAccessToken(token)).thenReturn(introspection);

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> authenticationController.profile(token));

    assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    verify(authenticationService).introspectAccessToken(token);
    verify(authenticationService, never()).profile(token);
  }

  @Test
  void profileWithNullIntrospectResponse() {
    String token = "invalid-token";

    when(authenticationService.introspectAccessToken(token)).thenReturn(null);

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> authenticationController.profile(token));

    assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    verify(authenticationService).introspectAccessToken(token);
    verify(authenticationService, never()).profile(token);
  }

  @Test
  void redirectWithHost() throws IOException {
    String host = "test-host.com";
    String expectedUrl = "expectedUrl";

    when(authenticationService.generateServiceURL(host)).thenReturn(expectedUrl);
    authenticationController.redirect(host, response);
    verify(response).sendRedirect(expectedUrl);
  }

  @Test
  void redirectWithoutHost() throws IOException {
    String expectedUrl = "expectedUrl";

    when(authenticationService.generateServiceURL(null)).thenReturn(expectedUrl);
    authenticationController.redirect(null, response);
    verify(response).sendRedirect(expectedUrl);
  }

  @Test
  void introspectWithValidToken() {

    try {
      String token = accessTokenHelper.provideAccessToken(userLogin, userPassword);
      OIDCIntrospectResponse response = notMockedAuthenticationController.introspect(token);
      assertNotNull(response, "Introspect response not nul vor valid token");
      assertEquals(token, response.getToken(), "Introspect response token");
      assertEquals(
          userLogin, response.getUniqueSecurityName(), "Introspect response uniqueSecurityName");
      assertTrue(response.isActive(), "Introspect response active");

    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }

  @Test
  void introspectWithInvalidToken() {

    try {
      String token = "invalid-token";
      OIDCIntrospectResponse response = notMockedAuthenticationController.introspect(token);
      assertNotNull(response, "Introspect response not nul vor valid token");
      assertNull(response.getUniqueSecurityName(), "Introspect null security name");
      assertFalse(response.isActive(), "Introspect not active");
    } catch (Exception e) {
      fail("Exception should not be thrown: " + e.getMessage());
    }
  }
}
