package fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.delegate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class SecurityDelegateTest {

  private SecurityDelegate securityDelegate;

  @Mock private SecurityContext securityContext;

  @Mock private Authentication authentication;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.setContext(securityContext);
    securityDelegate = new SecurityDelegate();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Nested
  class GivenSecurityDelegate {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a security delegate");
    }

    @Nested
    class WhenGettingAuthenticatedUserLogin {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting authenticated user login");
      }

      @Nested
      class AndAuthenticationIsValid {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("authentication is valid");

          when(securityContext.getAuthentication()).thenReturn(authentication);
          when(authentication.isAuthenticated()).thenReturn(true);
          when(authentication.getName()).thenReturn("user123");
        }

        @Test
        void thenItShouldReturnAuthenticatedUserLogin() {
          BddLogger.then("it should return authenticated user login");

          String result = securityDelegate.getAuthenticatedUserLogin();

          assertEquals("user123", result, "Login verification");
        }
      }

      @Nested
      class AndAuthenticationIsNotAuthenticated {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("authentication is not authenticated");

          when(securityContext.getAuthentication()).thenReturn(authentication);
          when(authentication.isAuthenticated()).thenReturn(false);
        }

        @Test
        void thenItShouldThrowForbiddenResponseStatusException() {
          BddLogger.then("it should throw forbidden response status exception");

          ResponseStatusException exception =
              assertThrows(
                  ResponseStatusException.class,
                  () -> securityDelegate.getAuthenticatedUserLogin());

          assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        }
      }

      @Nested
      class AndAuthenticationIsMissing {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("authentication is missing");

          when(securityContext.getAuthentication()).thenReturn(null);
        }

        @Test
        void thenItShouldThrowForbiddenResponseStatusException() {
          BddLogger.then("it should throw forbidden response status exception");

          ResponseStatusException exception =
              assertThrows(
                  ResponseStatusException.class,
                  () -> securityDelegate.getAuthenticatedUserLogin());

          assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        }
      }
    }
  }
}
