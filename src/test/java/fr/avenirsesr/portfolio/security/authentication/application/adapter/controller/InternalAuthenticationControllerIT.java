package fr.avenirsesr.portfolio.security.authentication.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.authentication.domain.exception.UnauthenticatedSessionException;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import fr.avenirsesr.portfolio.security.authentication.domain.model.SignedAuthContext;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service.AuthenticationSessionReader;
import fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.session.SessionAttributes;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class InternalAuthenticationControllerIT {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AuthenticationService authenticationService;

  @MockitoBean private AuthenticationSessionReader authenticationSessionReader;

  @Nested
  class GivenInternalAuthenticationController {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("an internal authentication controller");
    }

    @Nested
    class WhenGettingAuthenticationContext {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting authentication context");
      }

      @Nested
      class AndTheSessionIsAuthenticated {
        private OIDCSession oidcSession;
        private SignedAuthContext signedAuthContext;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the session is authenticated");

          oidcSession = oidcSession();
          signedAuthContext = signedAuthContext();

          when(authenticationSessionReader.readOidcSession(any()))
              .thenReturn(Optional.of(oidcSession));
          when(authenticationService.refreshSessionIfNeeded(oidcSession)).thenReturn(oidcSession);
          when(authenticationService.getSignedAuthenticatedContext(oidcSession))
              .thenReturn(signedAuthContext);
        }

        @Test
        void thenItShouldReturnSignedAuthContext() throws Exception {
          BddLogger.then("it should return signed auth context");

          mockMvc
              .perform(get("/internal/auth/context"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.payload").value(signedAuthContext.payload()))
              .andExpect(jsonPath("$.signature").value(signedAuthContext.signature()))
              .andExpect(jsonPath("$.kid").value(signedAuthContext.kid()));

          verify(authenticationSessionReader).readOidcSession(any());
          verify(authenticationService).refreshSessionIfNeeded(oidcSession);
          verify(authenticationService).getSignedAuthenticatedContext(oidcSession);
        }
      }

      @Nested
      class AndTheSessionHasBeenRefreshed {
        private OIDCSession oidcSession;
        private OIDCSession refreshedSession;
        private MockHttpSession httpSession;
        private SignedAuthContext signedAuthContext;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the OIDC session has been refreshed");

          oidcSession = oidcSession();
          refreshedSession =
              new OIDCSession(
                  "new-access-token",
                  "new-refresh-token",
                  "new-id-token",
                  Instant.parse("2026-05-13T14:30:00Z"));

          httpSession = new MockHttpSession();
          signedAuthContext = signedAuthContext();

          when(authenticationSessionReader.readOidcSession(any()))
              .thenReturn(Optional.of(oidcSession));
          when(authenticationService.refreshSessionIfNeeded(oidcSession))
              .thenReturn(refreshedSession);
          when(authenticationService.getSignedAuthenticatedContext(refreshedSession))
              .thenReturn(signedAuthContext);
        }

        @Test
        void thenItShouldStoreRefreshedSessionAndReturnSignedAuthContext() throws Exception {
          BddLogger.then("it should store refreshed session and return signed auth context");

          mockMvc
              .perform(get("/internal/auth/context").session(httpSession))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.payload").value(signedAuthContext.payload()))
              .andExpect(jsonPath("$.signature").value(signedAuthContext.signature()))
              .andExpect(jsonPath("$.kid").value(signedAuthContext.kid()));

          assertEquals(refreshedSession, httpSession.getAttribute(SessionAttributes.OIDC_SESSION));

          verify(authenticationSessionReader).readOidcSession(any());
          verify(authenticationService).refreshSessionIfNeeded(oidcSession);
          verify(authenticationService).getSignedAuthenticatedContext(refreshedSession);
        }
      }

      @Nested
      class AndThereIsNoOidcSession {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("there is no OIDC session");

          when(authenticationSessionReader.readOidcSession(any())).thenReturn(Optional.empty());
        }

        @Test
        void thenItShouldReturnUnauthorized() throws Exception {
          BddLogger.then("it should return unauthorized");

          mockMvc.perform(get("/internal/auth/context")).andExpect(status().isUnauthorized());

          verify(authenticationSessionReader).readOidcSession(any());
          verifyNoInteractions(authenticationService);
        }
      }

      @Nested
      class AndRefreshSessionThrowsUnauthenticatedSessionException {
        private OIDCSession oidcSession;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("refresh session throws unauthenticated session exception");

          oidcSession = oidcSession();

          when(authenticationSessionReader.readOidcSession(any()))
              .thenReturn(Optional.of(oidcSession));
          when(authenticationService.refreshSessionIfNeeded(oidcSession))
              .thenThrow(new UnauthenticatedSessionException());
        }

        @Test
        void thenItShouldReturnUnauthorized() throws Exception {
          BddLogger.then("it should return unauthorized");

          mockMvc.perform(get("/internal/auth/context")).andExpect(status().isUnauthorized());

          verify(authenticationSessionReader).readOidcSession(any());
          verify(authenticationService).refreshSessionIfNeeded(oidcSession);
        }
      }

      @Nested
      class AndAuthenticationServiceThrowsUnauthenticatedSessionException {
        private OIDCSession oidcSession;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("authentication service throws unauthenticated session exception");

          oidcSession = oidcSession();

          when(authenticationSessionReader.readOidcSession(any()))
              .thenReturn(Optional.of(oidcSession));
          when(authenticationService.refreshSessionIfNeeded(oidcSession)).thenReturn(oidcSession);
          when(authenticationService.getSignedAuthenticatedContext(oidcSession))
              .thenThrow(new UnauthenticatedSessionException());
        }

        @Test
        void thenItShouldReturnUnauthorized() throws Exception {
          BddLogger.then("it should return unauthorized");

          mockMvc.perform(get("/internal/auth/context")).andExpect(status().isUnauthorized());

          verify(authenticationSessionReader).readOidcSession(any());
          verify(authenticationService).refreshSessionIfNeeded(oidcSession);
          verify(authenticationService).getSignedAuthenticatedContext(oidcSession);
        }
      }
    }
  }

  private OIDCSession oidcSession() {
    return new OIDCSession(
        "access-token", "refresh-token", "id-token", Instant.parse("2026-05-13T13:30:00Z"));
  }

  private SignedAuthContext signedAuthContext() {
    return new SignedAuthContext(
        "{\"sub\":\"00000000-0000-0000-0000-000000000101\",\"iat\":1,\"exp\":301}",
        "signature",
        "v2");
  }
}
