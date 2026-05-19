package fr.avenirsesr.portfolio.security.authentication.application.adapter.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.session.SessionAttributes;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(
    properties = {
      "avenirs.authentication.oidc.logout.url=https://dev.avenirs-esr.fr/cas/logout",
      "avenirs.authentication.auth.logout.default-service=https://dev.avenirs-esr.fr/cofolio/student",
      "server.servlet.session.cookie.name=AVENIRS_SESSION"
    })
class AuthenticationControllerIT {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AuthenticationService authenticationService;

  @Nested
  class GivenAuthenticationController {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("an authentication controller");
    }

    @Nested
    class WhenLoginIsRequested {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("login is requested");
      }

      @Nested
      class AndHostAndSafeRedirectAreProvided {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("host and safe redirect are provided");

          when(authenticationService.generateAuthorizationUrl(
                  "dev.avenirs-esr.fr", "/cofolio/student"))
              .thenReturn("https://dev.avenirs-esr.fr/cas/oidc/oidcAuthorize");
        }

        @Test
        void thenItShouldRedirectToGeneratedAuthorizationUrl() throws Exception {
          BddLogger.then("it should redirect to generated authorization URL");

          mockMvc
              .perform(
                  get("/auth/login")
                      .header("x-forwarded-host", "dev.avenirs-esr.fr")
                      .param("redirect", "/cofolio/student"))
              .andExpect(status().is3xxRedirection())
              .andExpect(
                  header().string("Location", "https://dev.avenirs-esr.fr/cas/oidc/oidcAuthorize"));

          verify(authenticationService)
              .generateAuthorizationUrl("dev.avenirs-esr.fr", "/cofolio/student");
        }
      }

      @Nested
      class AndHostIsMissing {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("host is missing");

          when(authenticationService.generateAuthorizationUrl("localhost", "/cofolio/student"))
              .thenReturn("https://localhost/cas/oidc/oidcAuthorize");
        }

        @Test
        void thenItShouldUseLocalhost() throws Exception {
          BddLogger.then("it should use localhost");

          mockMvc
              .perform(get("/auth/login").param("redirect", "/cofolio/student"))
              .andExpect(status().is3xxRedirection())
              .andExpect(header().string("Location", "https://localhost/cas/oidc/oidcAuthorize"));

          verify(authenticationService).generateAuthorizationUrl("localhost", "/cofolio/student");
        }
      }

      @Nested
      class AndRedirectIsUnsafe {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("redirect is unsafe");

          when(authenticationService.generateAuthorizationUrl(
                  "dev.avenirs-esr.fr", "/cofolio/student"))
              .thenReturn("https://dev.avenirs-esr.fr/cas/oidc/oidcAuthorize");
        }

        @Test
        void thenItShouldUseDefaultRedirect() throws Exception {
          BddLogger.then("it should use default redirect");

          mockMvc
              .perform(
                  get("/auth/login")
                      .header("x-forwarded-host", "dev.avenirs-esr.fr")
                      .param("redirect", "https://evil.com"))
              .andExpect(status().is3xxRedirection())
              .andExpect(
                  header().string("Location", "https://dev.avenirs-esr.fr/cas/oidc/oidcAuthorize"));

          verify(authenticationService)
              .generateAuthorizationUrl("dev.avenirs-esr.fr", "/cofolio/student");
        }
      }
    }

    @Nested
    class WhenCallbackIsRequested {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("callback is requested");
      }

      @Nested
      class AndCodeIsMissing {

        @Test
        void thenItShouldReturnUnauthorized() throws Exception {
          BddLogger.then("it should return unauthorized");

          mockMvc
              .perform(
                  get("/auth/callback")
                      .header("x-forwarded-host", "dev.avenirs-esr.fr")
                      .param("state", "/cofolio/student"))
              .andExpect(status().isUnauthorized());
        }
      }

      @Nested
      class AndCodeIsBlank {

        @Test
        void thenItShouldReturnUnauthorized() throws Exception {
          BddLogger.then("it should return unauthorized");

          mockMvc
              .perform(
                  get("/auth/callback")
                      .header("x-forwarded-host", "dev.avenirs-esr.fr")
                      .param("code", " ")
                      .param("state", "/cofolio/student"))
              .andExpect(status().isUnauthorized());
        }
      }

      @Nested
      class AndCodeAndSafeStateAreProvided {
        private OIDCSession oidcSession;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("code and safe state are provided");

          oidcSession = oidcSession();

          when(authenticationService.createSessionFromAuthorizationCode(
                  "dev.avenirs-esr.fr", "code"))
              .thenReturn(oidcSession);
        }

        @Test
        void thenItShouldStoreOidcSessionAndRedirectToState() throws Exception {
          BddLogger.then("it should store OIDC session and redirect to state");

          mockMvc
              .perform(
                  get("/auth/callback")
                      .header("x-forwarded-host", "dev.avenirs-esr.fr")
                      .param("code", "code")
                      .param("state", "/cofolio/student"))
              .andExpect(status().is3xxRedirection())
              .andExpect(header().string("Location", "https://dev.avenirs-esr.fr/cofolio/student"))
              .andExpect(request().sessionAttribute(SessionAttributes.OIDC_SESSION, oidcSession));

          verify(authenticationService)
              .createSessionFromAuthorizationCode("dev.avenirs-esr.fr", "code");
        }
      }

      @Nested
      class AndStateIsUnsafe {
        private OIDCSession oidcSession;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("state is unsafe");

          oidcSession = oidcSession();

          when(authenticationService.createSessionFromAuthorizationCode(
                  "dev.avenirs-esr.fr", "code"))
              .thenReturn(oidcSession);
        }

        @Test
        void thenItShouldRedirectToDefaultPath() throws Exception {
          BddLogger.then("it should redirect to default path");

          mockMvc
              .perform(
                  get("/auth/callback")
                      .header("x-forwarded-host", "dev.avenirs-esr.fr")
                      .param("code", "code")
                      .param("state", "https://evil.com"))
              .andExpect(status().is3xxRedirection())
              .andExpect(header().string("Location", "https://dev.avenirs-esr.fr/cofolio/student"))
              .andExpect(request().sessionAttribute(SessionAttributes.OIDC_SESSION, oidcSession));
        }
      }
    }

    @Nested
    class WhenLogoutIsRequested {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("logout is requested");
      }

      @Nested
      class AndSessionExists {
        private MockHttpSession session;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("session exists");

          session = new MockHttpSession();
          session.setAttribute(SessionAttributes.OIDC_SESSION, oidcSession());
        }

        @Test
        void thenItShouldInvalidateSessionClearCookieAndRedirectToCasLogout() throws Exception {
          BddLogger.then("it should invalidate session, clear cookie and redirect to CAS logout");

          mockMvc
              .perform(get("/auth/logout").session(session))
              .andExpect(status().is3xxRedirection())
              .andExpect(
                  header()
                      .string(
                          "Location",
                          "https://dev.avenirs-esr.fr/cas/logout?service=https://dev.avenirs-esr.fr/cofolio/student"))
              .andExpect(cookie().maxAge("AVENIRS_SESSION", 0))
              .andExpect(cookie().httpOnly("AVENIRS_SESSION", true))
              .andExpect(cookie().secure("AVENIRS_SESSION", true))
              .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=Lax")));
        }
      }

      @Nested
      class AndSessionDoesNotExist {

        @Test
        void thenItShouldClearCookieAndRedirectToCasLogout() throws Exception {
          BddLogger.then("it should clear cookie and redirect to CAS logout");

          mockMvc
              .perform(get("/auth/logout"))
              .andExpect(status().is3xxRedirection())
              .andExpect(
                  header()
                      .string(
                          "Location",
                          "https://dev.avenirs-esr.fr/cas/logout?service=https://dev.avenirs-esr.fr/cofolio/student"))
              .andExpect(cookie().maxAge("AVENIRS_SESSION", 0));
        }
      }
    }
  }

  private OIDCSession oidcSession() {
    return new OIDCSession(
        "access-token", "refresh-token", "id-token", Instant.parse("2026-05-13T13:30:00Z"));
  }
}
